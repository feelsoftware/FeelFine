@file:SuppressLint("StaticFieldLeak")
@file:Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")

package com.feelsoftware.feelfine.ui.statistic

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.*
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.data.model.UserProfile
import com.feelsoftware.feelfine.data.repository.UserRepository
import com.feelsoftware.feelfine.extension.onClick
import com.feelsoftware.feelfine.extension.toLiveData
import com.feelsoftware.feelfine.fit.model.Duration
import com.feelsoftware.feelfine.fit.model.toHoursMinutes
import com.feelsoftware.feelfine.fit.model.total
import com.feelsoftware.feelfine.fit.usecase.GetFitDataUseCase
import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import com.feelsoftware.feelfine.ui.profile.DemoProfileBadge
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class StatisticFragment : BaseFragment<StatisticViewModel>(R.layout.fragment_statistic) {

    override val viewModel: StatisticViewModel by viewModel()

    // TODO: Replace with ViewBinding
    private inline val tvCurrentDate: TextView get() = requireView().findViewById(R.id.tvCurrentDate)
    private inline val tvPreviousCategory: TextView get() = requireView().findViewById(R.id.tvPreviousCategory)
    private inline val tvCurrentCategory: TextView get() = requireView().findViewById(R.id.tvCurrentCategory)
    private inline val tvNextCategory: TextView get() = requireView().findViewById(R.id.tvNextCategory)
    private inline val btnPreviousCategory: ImageView get() = requireView().findViewById(R.id.btnPreviousCategory)
    private inline val btnNextCategory: ImageView get() = requireView().findViewById(R.id.btnNextCategory)
    private inline val weekB: TextView get() = requireView().findViewById(R.id.weekB)
    private inline val monthB: TextView get() = requireView().findViewById(R.id.monthB)
    private inline val demoLabel: DemoProfileBadge get() = requireView().findViewById(R.id.demoLabel)
    private inline val btnPreviousDate: ImageView get() = requireView().findViewById(R.id.btnPreviousDate)
    private inline val btnNextDate: ImageView get() = requireView().findViewById(R.id.btnNextDate)
    private inline val chart: BarChart get() = requireView().findViewById(R.id.chart)

    override fun onReady() {
        viewModel.previousCategoryTitle.observe {
            tvPreviousCategory.text = it
            btnPreviousCategory.isInvisible = it == null
        }
        viewModel.currentCategoryTitle.observe { tvCurrentCategory.text = it }
        viewModel.nextCategoryTitle.observe {
            tvNextCategory.text = it
            btnNextCategory.isInvisible = it == null
        }
        tvPreviousCategory.onClick { viewModel.onPreviousCategoryClicked() }
        btnPreviousCategory.onClick { viewModel.onPreviousCategoryClicked() }
        tvNextCategory.onClick { viewModel.onNextCategoryClicked() }
        btnNextCategory.onClick { viewModel.onNextCategoryClicked() }

        viewModel.isWeekStyleData.observe { isWeekStyle ->
            weekB.updateTabSelection(isWeekStyle)
            monthB.updateTabSelection(isWeekStyle.not())
        }
        weekB.onClick { viewModel.onWeekStyleSelected() }
        monthB.onClick { viewModel.onMonthStyleSelected() }

        viewModel.currentDateTitle.observe { tvCurrentDate.text = it }
        btnPreviousDate.onClick { viewModel.onPreviousDateRangeClicked() }
        btnNextDate.onClick { viewModel.onNextDateRangeClicked() }

        viewModel.userProfile.observe { profile ->
            demoLabel.isVisible = profile.isDemo
        }

        initChart()
        viewModel.chartData.observe(::displayChartData)
    }

    private fun initChart() {
        chart.description.isEnabled = false
        chart.setMaxVisibleValueCount(60)
        chart.setPinchZoom(false)
        chart.setDrawGridBackground(false)
        chart.xAxis.apply {
            position = XAxisPosition.BOTTOM
            setDrawGridLines(false)
            granularity = 1f
        }
        chart.axisLeft.apply {
            setPosition(YAxisLabelPosition.OUTSIDE_CHART)
            spaceTop = 15f
            axisMinimum = 0f
        }
        chart.axisRight.isEnabled = false
        chart.legend.isEnabled = false
    }

    private fun displayChartData(chartData: ChartData) {
        chart.xAxis.valueFormatter = chartData.xFormatter
        chart.axisLeft.valueFormatter = chartData.yFormatter
        chart.axisRight.isEnabled = false

        chart.data = BarData(BarDataSet(chartData.data, ""))
        chart.data.setValueFormatter(chartData.yFormatter)
        chart.invalidate()
    }
}

class StatisticViewModel(
    private val context: Context,
    useCase: GetFitDataUseCase,
    userRepository: UserRepository,
) : BaseViewModel() {

    private val categories = listOf(
        Category.ACTIVITY,
        Category.STEPS,
        Category.SLEEP,
    )
    private var currentCategoryIndex: Int = -1
        set(value) {
            field = value
            updateCategories()
        }
    private val currentCategoryData = MutableLiveData(Category.STEPS)
    val previousCategoryTitle = MutableLiveData<String?>()
    val currentCategoryTitle: LiveData<String> = currentCategoryData.map { it.title }
    val nextCategoryTitle = MutableLiveData<String?>()

    val isWeekStyleData = MutableLiveData(true)

    private val calendar = Calendar.getInstance()
    private val currentDate = MutableLiveData<Date>()
    val currentDateTitle = currentDate.switchMap { date ->
        calendar.time = date

        isWeekStyleData.map { isWeekStyle ->
            if (isWeekStyle) {
                val (startTime, endTime) = getWeekDates(date)

                calendar.time = startTime
                val from = calendar.get(Calendar.DAY_OF_MONTH)
                val fromMonth = calendar.getDisplayName(
                    Calendar.MONTH, Calendar.SHORT, Locale.getDefault()
                )

                calendar.time = endTime
                val to = calendar.get(Calendar.DAY_OF_MONTH)
                val toMonth = calendar.getDisplayName(
                    Calendar.MONTH, Calendar.SHORT, Locale.getDefault()
                )

                if (fromMonth == toMonth) {
                    "$from - $to $fromMonth"
                } else {
                    "$from $fromMonth - $to $toMonth"
                }
            } else {
                SimpleDateFormat("LLLL", Locale.getDefault()).format(calendar.time)
            }
        }
    }

    val chartData: LiveData<ChartData> = currentCategoryData.switchMap { currentCategory ->
        isWeekStyleData.switchMap { isWeekStyle ->
            currentDate.switchMap { date ->
                val (startTime, endTime) = if (isWeekStyle) {
                    getWeekDates(date)
                } else {
                    getMonthDates(date)
                }

                when (currentCategory) {
                    Category.ACTIVITY -> useCase.getActivity(startTime, endTime)
                        .map {
                            it.toChartData(valueFormatter = {
                                Duration(it).toHoursMinutes()
                            }) { activityInfo ->
                                activityInfo.date to activityInfo.total.minutesTotal
                            }
                        }
                        .toLiveData()
                    Category.STEPS -> useCase.getSteps(startTime, endTime)
                        .map {
                            it.toChartData(valueFormatter = {
                                it.toString()
                            }) { stepsInfo ->
                                stepsInfo.date to stepsInfo.count
                            }
                        }
                        .toLiveData()
                    Category.SLEEP -> useCase.getSleep(startTime, endTime)
                        .map {
                            it.toChartData(valueFormatter = {
                                Duration(it).toHoursMinutes()
                            }) { sleepInfo ->
                                sleepInfo.date to sleepInfo.total.minutesTotal
                            }
                        }
                        .toLiveData()
                }
            }
        }
    }

    val userProfile = MutableLiveData<UserProfile>()

    init {
        currentCategoryIndex = 1
        currentDate.value = Date()
        userProfile.attachSource(userRepository.getProfileLegacy()) { it }
    }

    // region Category
    fun onPreviousCategoryClicked() = changeCategory(toNextCategory = false)

    fun onNextCategoryClicked() = changeCategory(toNextCategory = true)

    private fun changeCategory(toNextCategory: Boolean) {
        val i = if (toNextCategory) 1 else -1
        val newCategoryIndex = currentCategoryIndex + i
        if (newCategoryIndex < 0 || newCategoryIndex > categories.size) return
        currentCategoryIndex = newCategoryIndex
    }

    private fun updateCategories() {
        previousCategoryTitle.value = categories.getOrNull(currentCategoryIndex - 1)?.title
        currentCategoryData.value = categories[currentCategoryIndex]
        nextCategoryTitle.value = categories.getOrNull(currentCategoryIndex + 1)?.title
    }
    // endregion

    // region Week or Month
    fun onWeekStyleSelected() {
        if (isWeekStyleData.value == true) return
        isWeekStyleData.value = true
    }

    fun onMonthStyleSelected() {
        if (isWeekStyleData.value == false) return
        isWeekStyleData.value = false
    }
    // endregion

    // region Date range
    fun onPreviousDateRangeClicked() = changeDateRange(toNextDateRange = false)

    fun onNextDateRangeClicked() = changeDateRange(toNextDateRange = true)

    private fun changeDateRange(toNextDateRange: Boolean) {
        calendar.time = currentDate.value ?: Date()
        val i = if (toNextDateRange) 1 else -1
        if (isWeekStyleData.value == true) {
            calendar.add(Calendar.WEEK_OF_YEAR, i)
        } else {
            calendar.add(Calendar.MONTH, i)
        }
        currentDate.value = calendar.time
    }

    private fun getWeekDates(date: Date): Pair<Date, Date> {
        val startDate = Calendar.getInstance().apply {
            time = date
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            set(Calendar.HOUR_OF_DAY, getActualMinimum(Calendar.HOUR_OF_DAY))
            set(Calendar.MINUTE, getActualMinimum(Calendar.MINUTE))
            set(Calendar.SECOND, getActualMinimum(Calendar.SECOND))
            set(Calendar.MILLISECOND, getActualMinimum(Calendar.MILLISECOND))
        }.time
        val endDate = Calendar.getInstance().apply {
            time = startDate
            add(Calendar.WEEK_OF_YEAR, 1)
            add(Calendar.DAY_OF_WEEK, -1)
            set(Calendar.HOUR_OF_DAY, getActualMaximum(Calendar.HOUR_OF_DAY))
            set(Calendar.MINUTE, getActualMaximum(Calendar.MINUTE))
            set(Calendar.SECOND, getActualMaximum(Calendar.SECOND))
            set(Calendar.MILLISECOND, getActualMaximum(Calendar.MILLISECOND))
        }.time
        return startDate to endDate
    }

    private fun getMonthDates(date: Date): Pair<Date, Date> {
        val startDate = Calendar.getInstance().apply {
            time = date
            set(Calendar.DAY_OF_MONTH, getMinimum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, getActualMinimum(Calendar.HOUR_OF_DAY))
            set(Calendar.MINUTE, getActualMinimum(Calendar.MINUTE))
            set(Calendar.SECOND, getActualMinimum(Calendar.SECOND))
            set(Calendar.MILLISECOND, getActualMinimum(Calendar.MILLISECOND))
        }.time
        val endDate = Calendar.getInstance().apply {
            time = date
            set(Calendar.DAY_OF_MONTH, getMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, getActualMaximum(Calendar.HOUR_OF_DAY))
            set(Calendar.MINUTE, getActualMaximum(Calendar.MINUTE))
            set(Calendar.SECOND, getActualMaximum(Calendar.SECOND))
            set(Calendar.MILLISECOND, getActualMaximum(Calendar.MILLISECOND))
        }.time
        return startDate to endDate
    }
    // endregion

    // region Mapper
    private val Category.title: String
        get() = context.getString(titleResId).lowercase(Locale.getDefault())

    private fun <T> List<T>.toChartData(
        valueFormatter: (Int) -> String,
        mapper: (T) -> Pair<Date, Int>
    ): ChartData {
        val data = mapIndexed { index, t ->
            val value = mapper(t).second
            BarEntry(index.toFloat(), value.toFloat())
        }

        val dateFormatter = SimpleDateFormat("EEE d", Locale.ROOT)

        return ChartData(
            data = data,
            xFormatter = object : IndexAxisValueFormatter(map {
                val date = mapper(it).first
                dateFormatter.format(date)
            }) {
                override fun getFormattedValue(value: Float): String {
                    return values.getOrNull(value.toInt()) ?: ""
                }
            },
            yFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return valueFormatter(value.roundToInt())
                }
            }
        )
        // endregion
    }
}

data class ChartData(
    val data: List<BarEntry>,
    val xFormatter: ValueFormatter,
    val yFormatter: ValueFormatter
)

private enum class Category(@StringRes val titleResId: Int) {
    ACTIVITY(R.string.statistic_category_activity),
    STEPS(R.string.statistic_category_steps),
    SLEEP(R.string.statistic_category_sleep),
}

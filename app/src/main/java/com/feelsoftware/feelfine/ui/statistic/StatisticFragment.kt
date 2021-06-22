package com.feelsoftware.feelfine.ui.statistic

import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.MutableLiveData
import com.feelsoftware.feelfine.R
import com.feelsoftware.feelfine.fit.model.ActivityInfo
import com.feelsoftware.feelfine.fit.model.SleepInfo
import com.feelsoftware.feelfine.fit.model.StepsInfo
import com.feelsoftware.feelfine.fit.model.total
import com.feelsoftware.feelfine.fit.usecase.GetFitDataUseCase
import com.feelsoftware.feelfine.ui.base.BaseFragment
import com.feelsoftware.feelfine.ui.base.BaseViewModel
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.Legend.LegendForm
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import kotlinx.android.synthetic.main.fragment_statistic.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

private const val SCORE_ACTIVITY = 0
private const val SCORE_STEP = 1
private const val SCORE_SLEEP = 2

class StatisticFragment : BaseFragment<StatisticViewModel>(R.layout.fragment_statistic) {

    override val viewModel: StatisticViewModel by viewModel()

    private var currentScore = 1
    private var activeWeek = true
    private val activityList = listOf("activity", "steps", "sleep")

    private fun manageScore() {
        startTV.text = activityList.getOrNull(currentScore - 1)
        middleTV.text = activityList.getOrNull(currentScore)
        endTV.text = activityList.getOrNull(currentScore + 1)
        manageData()
    }

    override fun onReady() {
        initChart()
        manageScore()
        startIcon.setOnClickListener {
            if (currentScore > 0) {
                currentScore--
                manageScore()
            }
        }
        endIcon.setOnClickListener {
            if (currentScore < activityList.size - 1) {
                currentScore++
                manageScore()
            }
        }
        weekB.setOnClickListener {
            if (activeWeek) return@setOnClickListener
            activeWeek = true
            weekB.setTextColor(resources.getColor(R.color.white))
            monthB.setTextColor(resources.getColor(R.color.black))
            weekB.background =
                AppCompatResources.getDrawable(requireContext(), R.drawable.rounded_button)
            monthB.background = AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.rounded_period_background
            )
            manageData()
        }
        monthB.setOnClickListener {
            if (!activeWeek) return@setOnClickListener
            activeWeek = false
            monthB.setTextColor(resources.getColor(R.color.white))
            weekB.setTextColor(resources.getColor(R.color.black))
            monthB.background =
                AppCompatResources.getDrawable(requireContext(), R.drawable.rounded_button)
            weekB.background = AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.rounded_period_background
            )
            manageData()
        }

    }

    private fun manageData() {
        when (currentScore) {
            SCORE_ACTIVITY -> {
                if (activeWeek) {
                    viewModel.activityWeekData.observe {
                        setActivityData(it)
                    }
                } else {
                    viewModel.activityMonthData.observe {
                        setActivityData(it)
                    }
                }
            }
            SCORE_STEP -> {
                if (activeWeek) {
                    viewModel.stepsWeekData.observe {
                        setStepsData(it)
                    }
                } else {
                    viewModel.stepsMonthData.observe {
                        setStepsData(it)
                    }
                }
            }
            SCORE_SLEEP -> {
                if (activeWeek) {
                    viewModel.sleepWeekData.observe {
                        setSleepData(it)
                    }
                } else {
                    viewModel.sleepMonthData.observe {
                        setSleepData(it)
                    }
                }
            }
        }

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
        chart.axisRight.apply {
            setDrawGridLines(false)
            setLabelCount(8, false)
            spaceTop = 15f
            axisMinimum = 0f
        }
        val l = chart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)
        l.form = LegendForm.SQUARE
        l.formSize = 9f
        l.textSize = 11f
        l.xEntrySpace = 4f
    }

    private fun setStepsData(range: List<StepsInfo>) {
        chart.clear()
        val start = 0
        val values = ArrayList<BarEntry>()
        var i = start
        while (i <= start + range.size - 1) {
            val c = Calendar.getInstance()
            c.time = range[i].date
            val dayOfWeek = c[Calendar.DAY_OF_MONTH]
            val steps = range[i].count.toFloat()
            values.add(BarEntry(dayOfWeek.toFloat(), steps))
            i++
        }
        applyChart(values)
    }

    private fun setSleepData(range: List<SleepInfo>) {
        chart.clear()
        val start = 0
        val values = ArrayList<BarEntry>()
        var i = start
        while (i <= start + range.size - 1) {
            val c = Calendar.getInstance()
            c.time = range[i].date
            val dayOfWeek = c[Calendar.DAY_OF_MONTH]
            val duration = range[i].total.hours.toFloat()
            values.add(BarEntry(dayOfWeek.toFloat(), duration))
            i++
        }
        applyChart(values)
    }

    private fun setActivityData(range: List<ActivityInfo>) {
        chart.clear()
        val start = 0
        val values = ArrayList<BarEntry>()
        var i = start
        while (i <= start + range.size - 1) {
            val c = Calendar.getInstance()
            c.time = range[i].date
            val dayOfWeek = c[Calendar.DAY_OF_MONTH]
            val duration = range[i].total.hours.toFloat()
            values.add(BarEntry(dayOfWeek.toFloat(), duration))
            i++
        }
        applyChart(values)
    }

    private fun applyChart(values: ArrayList<BarEntry>) {
        val set1: BarDataSet
        if (chart.data != null &&
            chart.data.dataSetCount > 0
        ) {
            set1 = chart.data.getDataSetByIndex(0) as BarDataSet
            set1.values = values
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()
        } else {
            set1 = BarDataSet(values, "The year 2021")
            set1.setDrawIcons(false)
            val dataSets = ArrayList<IBarDataSet>()
            dataSets.add(set1)
            val data = BarData(dataSets)
            data.barWidth = 0.9f
            chart.data = data
            chart.notifyDataSetChanged()
        }
        chart.invalidate()
    }

}

class StatisticViewModel(useCase: GetFitDataUseCase) : BaseViewModel() {

    val stepsWeekData = MutableLiveData<List<StepsInfo>>()
    val stepsMonthData = MutableLiveData<List<StepsInfo>>()
    val sleepWeekData = MutableLiveData<List<SleepInfo>>()
    val sleepMonthData = MutableLiveData<List<SleepInfo>>()
    val activityWeekData = MutableLiveData<List<ActivityInfo>>()
    val activityMonthData = MutableLiveData<List<ActivityInfo>>()

    init {
        val (startDate, endDate) = getWeekDates(weekOffset = 0)
        val (startMonthDate, endMonthDate) = getMonthDates(monthOffset = 0)

        stepsWeekData.combine(useCase.getSteps(startDate, endDate)) {
            it
        }
        stepsMonthData.combine(useCase.getSteps(startMonthDate, endMonthDate)) {
            it
        }
        sleepWeekData.combine(useCase.getSleep(startDate, endDate)) {
            it
        }
        sleepMonthData.combine(useCase.getSleep(startMonthDate, endMonthDate)) {
            it
        }
        activityWeekData.combine(useCase.getActivity(startDate, endDate)) {
            it
        }
        activityMonthData.combine(useCase.getActivity(startMonthDate, endMonthDate)) {
            it
        }
    }

    private fun getWeekDates(weekOffset: Int): Pair<Date, Date> {
        val startDate = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, getMinimum(Calendar.DAY_OF_WEEK))
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            add(Calendar.WEEK_OF_YEAR, weekOffset)
        }.time
        val endDate = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, getMaximum(Calendar.DAY_OF_WEEK))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            add(Calendar.WEEK_OF_YEAR, weekOffset)
        }.time
        return startDate to endDate
    }

    private fun getMonthDates(monthOffset: Int): Pair<Date, Date> {
        val startDate = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, getMinimum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            add(Calendar.MONTH, monthOffset)
        }.time
        val endDate = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, getMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            add(Calendar.MONTH, monthOffset)
        }.time
        return startDate to endDate
    }
}

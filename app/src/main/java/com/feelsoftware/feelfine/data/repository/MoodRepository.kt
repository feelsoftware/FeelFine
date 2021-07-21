package com.feelsoftware.feelfine.data.repository

import com.feelsoftware.feelfine.data.db.dao.MoodDao
import com.feelsoftware.feelfine.data.db.entity.MoodEntity
import com.feelsoftware.feelfine.data.model.Mood
import com.feelsoftware.feelfine.data.model.MoodWithDate
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*

class MoodRepository(
    private val dao: MoodDao,
    private val dateConverter: MoodDateConverter,
) {

    fun setMood(mood: Mood): Completable =
        dao.insert(MoodEntity(mood, dateConverter.getCurrentDate()))
            .subscribeOn(Schedulers.io())

    fun getMoodByDay(date: Date): Observable<List<Mood>> =
        dao.getByDate(dateConverter.extractDay(date))
            .map { list ->
                list.map { it.mood }
            }
            .subscribeOn(Schedulers.io())

    fun getMoods(): Observable<List<MoodWithDate>> =
        dao.get()
            .map { list ->
                list.map { (mood, date) ->
                    MoodWithDate(mood, date)
                }
            }
            .subscribeOn(Schedulers.io())
}

private const val MOOD_DATE_HOURS = 12
private const val MOOD_DATE_MINUTES = 0

class MoodDateConverter {

    fun getCurrentDate(): Date =
        Calendar.getInstance().align().time

    fun extractDay(date: Date): Date =
        Calendar.getInstance().apply {
            time = date
            align()
        }.time

    private fun Calendar.align(): Calendar = apply {
        set(Calendar.HOUR_OF_DAY, MOOD_DATE_HOURS)
        set(Calendar.MINUTE, MOOD_DATE_MINUTES)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
}
@file:Suppress("HasPlatformType")

package com.feelsoftware.feelfine.data.repository

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.RawQuery
import com.feelsoftware.feelfine.data.db.entity.ActivityEntity
import com.feelsoftware.feelfine.data.db.entity.SleepEntity
import com.feelsoftware.feelfine.data.db.entity.StepsEntity
import com.feelsoftware.feelfine.fit.FitRepository
import com.feelsoftware.feelfine.fit.model.ActivityInfo
import com.feelsoftware.feelfine.fit.model.SleepInfo
import com.feelsoftware.feelfine.fit.model.StepsInfo
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import java.util.*

interface DataSource<T> {

    @RawQuery
    fun getAll(startTime: Date, endTime: Date): Single<List<T>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(list: List<T>): Completable

    @RawQuery
    fun delete(): Completable
}

abstract class DataRepository<L, T>(
    private val dataSource: String,
    private val localDataSource: DataSource<L>,
    private val remoteDataSource: DataSource<T>,
) {

    fun get(startTime: Date, endTime: Date): Observable<List<T>> = Observable.merge(
        localDataSource.getAll(startTime, endTime).map { it.toModels() }.toObservable()
            .subscribeOn(Schedulers.io())
            .doOnNext {
                Timber.d("[$dataSource] local read $it")
            },
        remoteDataSource.getAll(startTime, endTime).toObservable()
            .subscribeOn(Schedulers.io())
            .doOnNext {
                Timber.d("[$dataSource] remote read $it")
            }
            .flatMap { list ->
                localDataSource.insert(list.toLocals())
                    .doOnComplete {
                        Timber.d("[$dataSource] local write $list")
                    }
                    .subscribeOn(Schedulers.io())
                    // andThen for Completable doesn't work, toSingle & flatMapObservable is hotfix
                    .toSingle { }
                    .flatMapObservable { Observable.just(list) }
            }
    )
        .doOnSubscribe {
            Timber.d("[$dataSource] get $startTime $endTime")
        }
        .subscribeOn(Schedulers.io())

    abstract fun localToModel(local: L): T

    abstract fun modelToLocal(model: T): L

    private fun List<L>.toModels() = map { localToModel(it) }
    private fun List<T>.toLocals() = map { modelToLocal(it) }
}

// region FitData
class StepsDataRepository(
    localDataSource: DataSource<StepsEntity>,
    remoteDataSource: DataSource<StepsInfo>,
) : DataRepository<StepsEntity, StepsInfo>("steps", localDataSource, remoteDataSource) {

    override fun localToModel(local: StepsEntity) = with(local) {
        StepsInfo(date, count)
    }

    override fun modelToLocal(model: StepsInfo) = with(model) {
        StepsEntity(date, count)
    }
}

class StepsRemoteDataSource(
    private val fitRepository: FitRepository,
) : DataSource<StepsInfo> {

    override fun getAll(startTime: Date, endTime: Date): Single<List<StepsInfo>> =
        fitRepository.getSteps(startTime, endTime)

    override fun insert(list: List<StepsInfo>) = Completable.complete()

    override fun delete() = Completable.complete()
}

class SleepDataRepository(
    localDataSource: DataSource<SleepEntity>,
    remoteDataSource: DataSource<SleepInfo>,
) : DataRepository<SleepEntity, SleepInfo>("sleep", localDataSource, remoteDataSource) {

    override fun localToModel(local: SleepEntity) = with(local) {
        SleepInfo(date, lightSleep, deepSleep, awake, outOfBed)
    }

    override fun modelToLocal(model: SleepInfo) = with(model) {
        SleepEntity(date, lightSleep, deepSleep, awake, outOfBed)
    }
}

class SleepRemoteDataSource(
    private val fitRepository: FitRepository,
) : DataSource<SleepInfo> {

    override fun getAll(startTime: Date, endTime: Date): Single<List<SleepInfo>> =
        fitRepository.getSleep(startTime, endTime)

    override fun insert(list: List<SleepInfo>) = Completable.complete()

    override fun delete() = Completable.complete()
}

class ActivityDataRepository(
    localDataSource: DataSource<ActivityEntity>,
    remoteDataSource: DataSource<ActivityInfo>,
) : DataRepository<ActivityEntity, ActivityInfo>("activity", localDataSource, remoteDataSource) {

    override fun localToModel(local: ActivityEntity) = with(local) {
        ActivityInfo(date, activityUnknown, activityWalking, activityRunning)
    }

    override fun modelToLocal(model: ActivityInfo) = with(model) {
        ActivityEntity(date, activityUnknown, activityWalking, activityRunning)
    }
}

class ActivityRemoteDataSource(
    private val fitRepository: FitRepository,
) : DataSource<ActivityInfo> {

    override fun getAll(startTime: Date, endTime: Date): Single<List<ActivityInfo>> =
        fitRepository.getActivity(startTime, endTime)

    override fun insert(list: List<ActivityInfo>) = Completable.complete()

    override fun delete() = Completable.complete()
}
// endregion
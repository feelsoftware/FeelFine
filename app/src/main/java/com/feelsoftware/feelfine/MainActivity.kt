package com.feelsoftware.feelfine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.feelsoftware.feelfine.data.model.UserProfile
import com.feelsoftware.feelfine.data.repository.UserRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val repository: UserRepository by inject()
        Observable.interval(1, TimeUnit.SECONDS)
            .repeat()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMapCompletable {
                repository.setProfile(
                    UserProfile(
                        name = "Victor",
                        gender = UserProfile.Gender.values().random(),
                        weight = Random.nextFloat(),
                        age = Random.nextInt()
                    )
                )
            }
            .subscribe()
            .also(disposable::add)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}
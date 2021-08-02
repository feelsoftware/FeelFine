@file:Suppress("SameParameterValue", "MemberVisibilityCanBePrivate")

package com.feelsoftware.feelfine.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.*
import com.feelsoftware.feelfine.R
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*
import java.util.concurrent.TimeUnit

private const val PERIODICAL_TAG = "MoodTracker.PERIODICAL_TAG"
private const val PERIODICAL_NAME = "MoodTracker.PERIODICAL_NAME"
private const val TAG_HOUR = "MoodTracker.TAG_HOUR"

class MoodTracker(
    private val workManager: WorkManager
) : KoinComponent {

    fun trackPeriodically() {
        cancel()
        trackPeriodically(hour = 9)
    }

    fun cancel() {
        workManager.cancelAllWorkByTag(PERIODICAL_TAG)
    }

    private fun trackPeriodically(hour: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        val now = Date()
        if (calendar.time.before(now)) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        val delayTime = calendar.time.time - now.time
        val delayUnit = TimeUnit.MILLISECONDS

        val request = PeriodicWorkRequestBuilder<MoodTrackerWorker>(1L, TimeUnit.DAYS)
            .setInitialDelay(delayTime, delayUnit)
            .addTag(PERIODICAL_TAG)
            .addTag("$TAG_HOUR -> $hour")
            .build()
        workManager.enqueueUniquePeriodicWork(
            "$PERIODICAL_NAME $hour", ExistingPeriodicWorkPolicy.REPLACE, request
        )
    }
}

class MoodTrackerWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams), KoinComponent {

    private val notificationManager: MoodNotificationManager by inject()

    override fun doWork(): Result {
        notificationManager.show()
        return Result.success()
    }
}

private const val CHANNEL_ID = "MoodNotificationManager.CHANNEL_ID"
private const val NOTIFICATION_ID = 7171

class MoodNotificationManager(
    private val context: Context,
    private val notificationManager: NotificationManager
) {

    fun show() {
        createChannel()

        val pendingIntent = NavDeepLinkBuilder(context)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.moodFragment)
            .createPendingIntent()

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_mood_notification)
            .setContentTitle(context.getString(R.string.mood_notification_title))
            .setContentText(context.getString(R.string.mood_notification_message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.mood_score),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }
}

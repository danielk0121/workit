package dev.danielk.workit

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager

class WorkitApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "운동 타이머",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "운동 진행 중 표시되는 알림입니다"
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_ID = "workout_timer_channel"
    }
}

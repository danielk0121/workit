package dev.danielk.workit.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dev.danielk.workit.MainActivity
import dev.danielk.workit.R
import dev.danielk.workit.WorkitApplication
import java.util.Calendar

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, WorkitApplication.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Workit 리마인더")
            .setContentText("오늘 운동은 어떠신가요? 지금 시작해보세요! 💪")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            try {
                notify(2001, builder.build())
            } catch (e: SecurityException) {
                // Handle permission issue
            }
        }
    }
}

object ReminderManager {
    private const val ALARM_REQUEST_CODE = 1002

    fun scheduleDailyReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 20) // Default to 8 PM
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            
            // If already passed today, schedule for tomorrow
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    fun cancelDailyReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, ALARM_REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}

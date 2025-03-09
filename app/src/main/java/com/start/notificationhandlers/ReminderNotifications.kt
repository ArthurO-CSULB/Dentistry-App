package com.start.notificationhandlers

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.dentalhygiene.R
import java.text.SimpleDateFormat
import java.util.*

class NotificationHelper(private val context: Context) {

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleNotification(
        eventID: String,
        timeInMillis: Long,
        title: String,
        description: String
    ) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("eventID", eventID)
            putExtra("title", title)
            putExtra("description", description)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            eventID.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            pendingIntent
        )
    }

    // Parse date and time to milliseconds
    private fun parseDateTime(dateTime: String): Long {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return sdf.parse(dateTime)?.time ?: 0L
    }

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Create and show notification immediately
    fun showNotification(eventId: String, title: String, description: String) {
        val notification = NotificationCompat.Builder(context, "EVENT_NOTIFICATION_CHANNEL")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(eventId.hashCode(), notification)
    }

    // Function to cancel a scheduled notification
    fun cancelScheduledNotification(eventId: String) {
        notificationManager.cancel(eventId.hashCode())
    }
}


// BroadcastReceiver for handling notifications
class NotificationReceiver : BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Reminder"
        val description = intent.getStringExtra("description") ?: "Your event is soon."

        // Display the notification
        val notification = NotificationCompat.Builder(context, "event_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(title.hashCode(), notification)
    }
}

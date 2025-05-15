package com.start.notificationhandlers

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.PendingIntent
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.dentalhygiene.R
import java.text.SimpleDateFormat
import java.util.*
import android.Manifest
import android.content.pm.PackageManager
import android.app.Activity
import androidx.core.app.ActivityCompat
import android.content.SharedPreferences

class NotificationHelper(private val context: Context) {

    init {
        createNotificationChannel()  // Call the function in the init block
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "EVENT_NOTIFICATION_CHANNEL",
                "Event Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for event notifications"
            }
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleNotification(
        eventID: String,
        timeInMillis: Long,
        title: String,
        description: String
    ) {
        // Check for perms
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
                return
            }
        }

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("eventID", eventID)
            putExtra("title", title)
            putExtra("description", description)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            eventID.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
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
        val notification = NotificationCompat.Builder(context, "EVENT_NOTIFICATION_CHANNEL")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(title.hashCode(), notification)
    }
}

class InactivityNotificationHelper(private val context: Context) {
    companion object {
        private const val PREF_NAME = "InactivityPrefs"
        private const val LAST_ACTIVE_TIME = "last_active_time"
        private const val INACTIVITY_THRESHOLD = 3 * 24 * 60 * 60 * 1000L // 3 days in milliseconds
        private const val INACTIVITY_NOTIFICATION_ID = 9999
        private const val REQUEST_CODE = 1001
    }

    private val sharedPref: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    // Call this when user performs any activity in the app
    fun recordUserActivity() {
        sharedPref.edit().putLong(LAST_ACTIVE_TIME, System.currentTimeMillis()).apply()
        cancelInactivityNotification() // Cancel any pending notification since user is active
    }

    // Check if user has been inactive and schedule notification if needed
    fun checkInactivityAndNotify() {
        val lastActiveTime = sharedPref.getLong(LAST_ACTIVE_TIME, System.currentTimeMillis())
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastActiveTime > INACTIVITY_THRESHOLD) {
            showInactivityNotification()
        } else {
            // Schedule notification for when the threshold will be reached
            scheduleInactivityNotification(lastActiveTime + INACTIVITY_THRESHOLD)
        }
    }

    private fun scheduleInactivityNotification(triggerTime: Long) {
        val intent = Intent(context, InactivityNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }

    // Changed from private to public
    fun showInactivityNotification() {
        val notification = NotificationCompat.Builder(context, "EVENT_NOTIFICATION_CHANNEL")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("We miss you!")
            .setContentText("It's been a while since you used our app. Come back and check what's new!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.notify(INACTIVITY_NOTIFICATION_ID, notification)
    }

    private fun cancelInactivityNotification() {
        val intent = Intent(context, InactivityNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }

        // Also cancel any shown notification
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.cancel(INACTIVITY_NOTIFICATION_ID)
    }
}

class InactivityNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        InactivityNotificationHelper(context.applicationContext).showInactivityNotification()
    }
}
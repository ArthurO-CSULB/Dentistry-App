package com.start.notificationhandlers
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.dentalhygiene.R
import com.start.pages.NotificationReceiver
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.random.Random

/*
Timer Notification Handler to handle the different notifications



Author Referenced for structure of this code and how to make notification: Meet Patadia
URL: https://meetpatadia9.medium.com/local-notification-in-android-with-jetpack-compose-437b430710f3
 */
class TimerNotificationHandler(private val context: Context) {
    // notificationManager obtains the NotificationManager service, allowing us to interact with
    // notifications registered with the manager.
    private val notificationManager = context.getSystemService(NotificationManager::class.java)
    // notificationChannelTimer holds the ID of the notification channel, ensuring that the
    // notification is associated with the correct channel.
    private val notificationChannelTimer = "notification_channel_timer"

    // Notification for when the timer is finished.
    fun timerFinishedNotification() {
        // Declare a notification and build the notification with the channel id.
        // We set the icon, title, text, priority, set auto cancel so that notification cancels
        // upon user tap.
        val notification = NotificationCompat.Builder(context, notificationChannelTimer)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Morale")
            .setContentText("Brushing is all done!")
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            // finalizes the creation
            .build()

        // This line triggers the display of the notification by using the notificationManager to
        // notify the user. Random.nextInt() generates a unique
        // identifier for the notification, ensuring that each notification is treated as a
        // separate instance.
        notificationManager.notify(Random.nextInt(), notification)
    }

    // Overloaded function of timerFinishedNotification() for Calendar notifications
    fun timerFinishedNotification(title: String, description: String) {
        val notification = NotificationCompat.Builder(context, notificationChannelTimer)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            // finalizes the creation
            .build()

        notificationManager.notify(Random.nextInt(), notification)
    }

    // Functions for adding/removing reminders in Calendar
    // Function that schedules a notification
    @SuppressLint("ScheduleExactAlarm")
    fun scheduleNotification(eventID: String, eventTimeInMillis: Long, title: String, description: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Log for debugging
        Log.d("TimerNotificationHandler", "Scheduling notification for event $eventID at time: $eventTimeInMillis")

        // Create an Intent that points to NotificationReceiver
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("eventID", eventID)
            putExtra("title", title)
            putExtra("description", description)
        }

        // Create PendingIntent that will trigger NotificationReceiver
        val pendingIntent = PendingIntent.getBroadcast(context, eventID.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // Schedule notification to trigger at the event time
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, eventTimeInMillis, pendingIntent)
    }

    // If user edits event and changes time, cancel the previously set notification
    fun cancelScheduledNotification(eventID: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)

        // Target the eventID
        intent.putExtra("eventID", eventID)

        val pendingIntent = PendingIntent.getBroadcast(context, eventID.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // Cancel the scheduled notification
        alarmManager.cancel(pendingIntent)
    }
}

// BroadcastReceiver to handle when the alarm goes off and show the notification
class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Event Reminder"
        val description = intent.getStringExtra("description") ?: "Your event is soon."

        // Log for debugging
        Log.d("NotificationReceiver", "Received notification for event with title: $title")

        // Trigger the notification when the alarm goes off
        val notificationHandler = TimerNotificationHandler(context)
        notificationHandler.timerFinishedNotification(title, description)
    }
}

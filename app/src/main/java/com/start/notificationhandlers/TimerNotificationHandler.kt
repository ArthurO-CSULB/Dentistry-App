package com.start.notificationhandlers
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.dentalhygiene.R
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
            .setContentTitle("mOral")
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
}

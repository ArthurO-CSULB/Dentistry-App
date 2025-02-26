package com.start

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi

/*
Application class which will perform various actions when the app is created.

We use the android features 'NotificationChannel' and 'NotificationManager' to create our
notifications. We will have a notification channel for each of our features that utilize
notifications. Notification channels will be registered to our notification manager. We will
create separate notification handlers for features that utilize notifications which will describe
the various notifications for that specific feature.

Author Referenced for structure of this code and how to make notification: Meet Patadia
URL: https://meetpatadia9.medium.com/local-notification-in-android-with-jetpack-compose-437b430710f3
*/

// DentalApplication that extends the Application class, signifying that it's an
// application-level class, allowing it to perform actions when the app is created.
@RequiresApi(Build.VERSION_CODES.O)
class DentalApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // notificationChannel creates a NotificationChannel object. Where, notification_channel_id
        // is a unique identifier for the channel. Notification name is the display name of the
        // channel.

        // Notification channel for the timer.
        val timerNotificationChannel = NotificationChannel(
            "notification_channel_timer",
            "timer_notifications",
            NotificationManager.IMPORTANCE_HIGH
        )
        // notificationManager obtains the NotificationManager service, which is responsible for
        // managing notifications.
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Setting up the channel
        notificationManager.createNotificationChannel(timerNotificationChannel)
    }
}
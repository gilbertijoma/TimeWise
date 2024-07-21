package edu.towson.cosc435.ijoma.timewise

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class Alarm() : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            // Call the displayNotification function with data from the intent
            displayNotification(
                context,
                intent?.getStringExtra("title") ?: " ",
                intent?.getStringExtra("date") ?: " ",
                intent?.getStringExtra("time") ?: " "
            )
        } catch (ex: Exception) {
            // Log any exceptions that occur during notification handling
            Log.d("Received example?", "ex${ex.printStackTrace()}")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    public fun displayNotification(context: Context?, title: String, date: String, time: String) {
        // Get the notification manager service
        val manager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val CHANNEL_ID = "ch-1"
        val CHANNEL_NAME = "reminder_notification"

        // Create a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }

        // Create an intent to open notification when clicked
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Reminder: $title")
            .setContentText("You have set a reminder for: $date @ $time")
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)
            .build()

        // Notify the user with the built notification
        manager.notify(1, notification)
    }
}

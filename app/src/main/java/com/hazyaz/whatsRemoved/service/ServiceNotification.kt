/*
 * Copyright (c) Code Developed by Prof. Fabio Ciravegna
 * All rights Reserved
 */

package com.hazyaz.whatsRemoved.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat.*
import androidx.core.app.NotificationManagerCompat.IMPORTANCE_MIN
import com.hazyaz.whatsRemoved.MainActivity
import com.hazyaz.whatsRemoved.R

/**
 * Created by Iman on 08/09/2016.
 */
class ServiceNotification @JvmOverloads constructor(
    private var context: Context,
    private val mId: Int,
    runningInBackground: Boolean = false
) {
    private var notificationBuilder: Builder? = null
    var notification: Notification? = null
    private var notificationPendingIntent: PendingIntent? = null
    private var notificationManager: NotificationManager? = null

    companion object {
        private val TAG : String = Notification::class.java.simpleName
        private val CHANNEL_ID = TAG
        var notificationIcon = 0
        var notificationTitle: String? = null
        var thoughtOfTheDay: String? = "WhatsRemoved will save deleted Whatsapp messages"
        var notificationText: String? = null

    }

    init {
        val intent = Intent(context, MainActivity::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationPendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }

        notification = if (runningInBackground) {
            val message = "WhatsRemoved will save deleted Whatsapp messages"
            setNotification(context, message)
        } else {
            val thoughOfTheDay =
                thoughtOfTheDay
            val notificationTitle =
                notificationTitle
            val notificationIcon =
                notificationIcon
            setNotification(context, notificationTitle, thoughOfTheDay, notificationIcon)
        }
    }
    /**
     * This is the method that can be called to update the ServiceNotification
     */
    private fun setNotification(context: Context, title: String?, text: String?, icon: Int): Notification {
        val notification: Notification
        notificationManager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // OREO
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "Permanent ServiceNotification"
            val importance: Int = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            val description = "I would like to receive travel alerts and notifications for:"
            channel.description = description
            notificationBuilder = Builder(context, CHANNEL_ID)
            if (notificationManager != null) {
                notificationManager!!.createNotificationChannel(channel)
            }
            notification =
                notificationBuilder!!
                    .setSmallIcon(R.drawable.start)
//                    .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                    .setContentTitle(title)
                    .setContentText(text)
                    .setAutoCancel(false)
                    .setContentIntent(notificationPendingIntent)
                    .setVisibility(VISIBILITY_PRIVATE)
                    .build()
        } else {
            notification = Builder(
                context,
                "channelID"
            )
                .setSmallIcon(R.drawable.start)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) NotificationManager.IMPORTANCE_MIN
                    else IMPORTANCE_MIN
                )
                .setAutoCancel(false)
                .setContentIntent(notificationPendingIntent)
                .setVisibility(VISIBILITY_PRIVATE)
                .build()
        }
        return notification
    }


    private fun setNotification(context: Context, text: String?): Notification {
        return setNotification(context, notificationTitle, text, notificationIcon)
    }

}
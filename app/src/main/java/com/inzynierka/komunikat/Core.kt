package com.inzynierka.komunikat

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.inzynierka.komunikat.activities.ThreadsActivity
import com.inzynierka.komunikat.activities.friends.FriendsActivity
import com.inzynierka.komunikat.classes.FriendsRequestState
import com.inzynierka.komunikat.classes.User
import com.inzynierka.komunikat.utils.FirebaseUtils
import com.inzynierka.komunikat.utils.TOAST_CURRENT_USER_UID_NON_EXISTS

class Core : Application() {

    private var observingFriendsRequests = false

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val notificationChannel =
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                    name = NOTIFICATION_NAME
                    description = NOTIFICATION_DESCRIPTION
                }

            val manager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            manager.createNotificationChannel(notificationChannel)
        }
    }

    fun observeFriendsRequests() {
        if (observingFriendsRequests)
            return

        FirebaseUtils.requireCurrentUser { currentUser ->
            if (currentUser == null) {
                Log.e(TAG, TOAST_CURRENT_USER_UID_NON_EXISTS)
                return@requireCurrentUser
            }

            // Obserwowanie listy znajomych ze statusem RECEIVED_AWAITING
            FirebaseUtils.observeFriendsList(
                currentUser.uid,
                FriendsRequestState.RECEIVED_AWAITING
            ) {
                it.forEach { user -> showNotificationFromUser(user) }
            }
        }

        observingFriendsRequests = true
    }

    @SuppressLint("MissingPermission")
    private fun showNotificationFromUser(user: User) {
        val intent = Intent(this, FriendsActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )
        }

        val bitmap = BitmapFactory.decodeResource(
            applicationContext.resources,
            R.drawable.round_btn
        )

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.round_btn)
            .setContentTitle(NOTIFICATION_TITLE)
            .setContentText("Użytkownik ${user.name} zaprasza Cię do znajomych")
            .setLargeIcon(bitmap)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            notify(
                (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
                notificationBuilder.build()
            )
        }

    }

    companion object {
        private const val TAG = "Core"
        private const val CHANNEL_ID = "CHANNEL_ID"
        private const val CHANNEL_NAME = "CHANNEL_NAME"
        private const val NOTIFICATION_NAME = "Zaproszenia"
        private const val NOTIFICATION_DESCRIPTION = "Komunikaty o zaproszeniach do znajomości"
        private const val NOTIFICATION_TITLE = "Nowe zaproszenie"

    }
}

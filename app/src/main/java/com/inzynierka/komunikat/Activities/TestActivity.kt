package com.inzynierka.komunikat.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.database.FirebaseDatabase
import com.inzynierka.komunikat.R
import com.inzynierka.komunikat.classes.Friend
import kotlinx.android.synthetic.main.activity_test.*

class TestActivity : AppCompatActivity() {

    val channelId = "channel_example_01"
    val notificationId = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        createChannel()

        test_btn.setOnClickListener {
            pushNotification()
        }

        add_friend_btn.setOnClickListener {
            addToFriends()
        }

    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val name = "Test notification"
            val descriptionTxt = "Notification description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionTxt
            }
            val manager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun pushNotification() {
        val intent = Intent(this, ThreadsActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val bitmap =
            BitmapFactory.decodeResource(applicationContext.resources, R.drawable.round_btn)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.round_btn)
            .setContentTitle("Test")
            .setContentText("Hello")
            .setLargeIcon(bitmap)
            //.setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, notificationBuilder.build())
        }
    }

    private fun addToFriends() {
        val currentUser = "friend_1_uid" //FirebaseAuth.getInstance().uid
        val newFriend = "friend_3_uid" //tutaj weź uid wybranego użytkownika
        val friend1: Friend = Friend(currentUser, "polykarp200")
        val friend2: Friend = Friend(newFriend, "xXxMasterxXx")

        val ref = FirebaseDatabase.getInstance().getReference("/friends/$currentUser").push()
        ref.setValue(friend2)

        val ref2 = FirebaseDatabase.getInstance().getReference("/friends/$newFriend").push()
        ref2.setValue(friend1)

        //ref.addChildEventListener(object : ChildEventListener {}
    }
}
package com.inzynierka.komunikat.Activities.messages

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.inzynierka.komunikat.Activities.auth.RegisterActivity
import com.inzynierka.komunikat.Activities.messages.NewMessageActivity.Companion.USER_KEY
import com.inzynierka.komunikat.Activities.profile.ProfileActivity
import com.inzynierka.komunikat.Core
import com.inzynierka.komunikat.R
import com.inzynierka.komunikat.classes.Message
import com.inzynierka.komunikat.classes.MsgThread
import com.inzynierka.komunikat.listeners.SimpleChildEventListener
import com.inzynierka.komunikat.utils.TOAST_PERMISSIONS_NOT_ALL_GRANTED
import com.inzynierka.komunikat.utils.makeToastShow
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_threads.*

class ThreadsActivity : AppCompatActivity() {

    private val adapter = GroupAdapter<GroupieViewHolder>()
    private val lastMsgMap = HashMap<String, Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_threads)

        verifyIfUserIsLoggedIn()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionPostNotificationsForTiramisu()
        } else {
            onPermissionPostNotificationGranted()
        }

        setupRecyclerView()

        updateLatestMessage()
    }

    private fun setupRecyclerView() {
        threads_recycler_view.adapter = adapter
        threads_recycler_view.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )

        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra(USER_KEY, (item as MsgThread).userChattingWith)
            startActivity(intent)
        }
    }

    private fun onPermissionPostNotificationGranted() {
        (applicationContext as Core).observeFriendsRequests()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestPermissionPostNotificationsForTiramisu() {
        val permission = Manifest.permission.POST_NOTIFICATIONS
        val permissionGranted = PackageManager.PERMISSION_GRANTED

        if (ActivityCompat.checkSelfPermission(this, permission) != permissionGranted) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission),
                Companion.REQUEST_PERMISSION_POST_NOTIFICATIONS
            )
        } else {

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        if (requestCode == Companion.REQUEST_PERMISSION_POST_NOTIFICATIONS) {
            grantResults.forEach { grantResult ->
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    makeToastShow(TOAST_PERMISSIONS_NOT_ALL_GRANTED)
                    finish()
                }

                onPermissionPostNotificationGranted()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun verifyIfUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //switch po itemach w menu nawigacyjnym
        return when (item.itemId) {
            R.id.goToAccount -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
                true // potwierdzenie, że ta opcja została
            }
            R.id.menu_new_msg -> {
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.navigation_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun updateLatestMessage() {
        val usrUid = FirebaseAuth.getInstance().uid
        val reference = FirebaseDatabase.getInstance().getReference("/last_messages/$usrUid")

        reference.addChildEventListener(object : SimpleChildEventListener() {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message: Message = snapshot.getValue(Message::class.java) ?: return
                lastMsgMap[snapshot.key!!] = message
                updateRecyclerView()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java) ?: return
                lastMsgMap[snapshot.key!!] = message
                updateRecyclerView()
            }
        })
    }

    private fun updateRecyclerView() {
        adapter.clear()
        lastMsgMap.values.forEach { adapter.add(MsgThread(it)) }
    }

    companion object {
        private const val REQUEST_PERMISSION_POST_NOTIFICATIONS = 100
    }
}
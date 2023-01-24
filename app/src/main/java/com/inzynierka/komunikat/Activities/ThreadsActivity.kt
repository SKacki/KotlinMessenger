package com.inzynierka.komunikat.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.inzynierka.komunikat.R
import com.inzynierka.komunikat.activities.NewMessageActivity.Companion.USER_KEY
import com.inzynierka.komunikat.classes.Message
import com.inzynierka.komunikat.classes.MsgThread
import com.inzynierka.komunikat.listeners.SimpleChildEventListener
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

        threads_recycler_view.adapter = adapter
        threads_recycler_view.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )

        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra(USER_KEY, (item as MsgThread).userChattingWith)
            startActivity(intent)
        }

        updateLatestMessage()
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
}
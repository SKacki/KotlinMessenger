package com.inzynierka.komunikat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.inzynierka.komunikat.classes.message
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.chat_msg_from_row.view.*
import kotlinx.android.synthetic.main.chat_msg_to_row.view.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = user?.name

        val adapter = GroupAdapter<GroupieViewHolder>()
        recycler_view_chat.adapter = adapter
        //czujka do nowych wiadomości; jak nie działa to przenies wywołanie funkcji linijkę wyżej
        refreshMessages()
        //wysyłanie wiadomości
        send_btn_chat.setOnClickListener {
            sendMsg()
        }

    }

    private fun sendMsg()
    {
        //referencja do bazy danych
        val ref = FirebaseDatabase.getInstance().getReference("/messages").push()

        //dane do zbudowania obiektu wiadomość
        val msgId = ref.key
        val fromId = FirebaseAuth.getInstance().uid
        val toId = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)?.uid
        val txt = enter_msg_chat.text.toString()

        //zabezpieczenie na wypadek nulli w którymś z id
        if(fromId == null || toId == null) {return}

        val msgObj = message(msgId!!, fromId, toId, txt,  System.currentTimeMillis())
        ref.setValue(msgObj)
            .addOnSuccessListener {
                Log.d("Chat", "wysyłam wiadomość do bazy danych")
            }

    }

    private fun refreshMessages()
    {
        val ref = FirebaseDatabase.getInstance().getReference("/messages")
        ref.addChildEventListener(object : ChildEventListener
            {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val msg = snapshot.getValue(message::class.java)
                }
                //tych funkcji nie nadpisuję, ale trzeba je zaimplementować
                override fun onCancelled(error: DatabaseError) {}
                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
            })
    }
}

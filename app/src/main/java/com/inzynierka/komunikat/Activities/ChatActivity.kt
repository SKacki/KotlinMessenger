package com.inzynierka.komunikat.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.inzynierka.komunikat.NewMessageActivity
import com.inzynierka.komunikat.R
import com.inzynierka.komunikat.classes.ChatMsgFromItem
import com.inzynierka.komunikat.classes.ChatMsgToItem
import com.inzynierka.komunikat.classes.User
import com.inzynierka.komunikat.classes.message
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.chat_msg_from_row.*


class ChatActivity : AppCompatActivity() {

    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = user?.name

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
        enter_msg_chat.text.clear()

    }

    private fun refreshMessages()
    {
        val ref = FirebaseDatabase.getInstance().getReference("/messages")
        ref.addChildEventListener(object : ChildEventListener
            {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val msg = snapshot.getValue(message::class.java)

                    if(msg != null)
                    {
                        if(msg.fromId == FirebaseAuth.getInstance().uid) {
                            adapter.add(ChatMsgFromItem(msg.text))
                        }
                        else {
                            adapter.add(ChatMsgToItem(msg.text))
                        }
                    }

                }
                //tych funkcji nie nadpisuję, ale trzeba je zaimplementować
                override fun onCancelled(error: DatabaseError) {}
                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
            })
    }
}

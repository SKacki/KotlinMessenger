package com.inzynierka.komunikat.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.inzynierka.komunikat.R
import com.inzynierka.komunikat.classes.ChatMsgFromItem
import com.inzynierka.komunikat.classes.ChatMsgToItem
import com.inzynierka.komunikat.classes.User
import com.inzynierka.komunikat.classes.message
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chat.*



class ChatActivity : AppCompatActivity() {

    val adapter = GroupAdapter<GroupieViewHolder>()
    var recipient : User? = null
    val sender : User? = ThreadsActivity.user //nie wiem czy to czegoś nie wysadzi :)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        recipient = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = recipient?.name

        recycler_view_chat.adapter = adapter

        //czujka do nowych wiadomości;
        refreshMessages()
        //wysyłanie wiadomości
        send_btn_chat.setOnClickListener {
            sendMsg()
        }

    }

    private fun sendMsg()
    {
        //dane do zbudowania obiektu wiadomość
        val fromId = FirebaseAuth.getInstance().uid
        val toId = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)?.uid
        val txt = enter_msg_chat.text.toString()

        //zabezpieczenie na wypadek nulli w którymś z id
        if(fromId == null || toId == null) {return}
        //referencja do bazy danych
        val refSender = FirebaseDatabase.getInstance().getReference("/messages/$fromId/$toId").push()
        val refRecipient = FirebaseDatabase.getInstance().getReference("/messages/$toId/$fromId").push()

        val msgObj = message(refSender.key!!, fromId, toId, txt,  System.currentTimeMillis())
        refSender.setValue(msgObj)
        refRecipient.setValue(msgObj)
        enter_msg_chat.text.clear()
        recycler_view_chat.scrollToPosition(adapter.itemCount - 1)

    }

    private fun refreshMessages()
    {
        //sender/recipient = fromId/toId
        val ref = FirebaseDatabase.getInstance().getReference("/messages/${sender?.uid}/${recipient?.uid}")

        ref.addChildEventListener(object : ChildEventListener
            {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val msg = snapshot.getValue(message::class.java)

                    if(msg != null)
                    {
                        if(msg.fromId == FirebaseAuth.getInstance().uid) {
                            adapter.add(ChatMsgFromItem(msg.text, sender!!))
                        }
                        else {
                            adapter.add(ChatMsgToItem(msg.text, recipient!!))
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

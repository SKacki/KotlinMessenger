package com.inzynierka.komunikat.classes

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.inzynierka.komunikat.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_msg_from_row.view.*
import kotlinx.android.synthetic.main.chat_msg_to_row.view.*
import kotlinx.android.synthetic.main.threads_row.view.*

//klasa wiadomość - ułatwia przesyłanie info do bazy danych;
data class Message(
    val msgId: String = "",
    val fromId: String = "",
    val toId: String = "",
    val text: String = "",
    val timeStamp: Long = 0,
)

//klasa wiadomość od kogoś
class ChatMsgFromItem(val text: String, val usr: User) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.msg_from.text = text
        Picasso.get().load(usr.photoUrl).into(viewHolder.itemView.msg_from_usr_pic)
    }

    override fun getLayout(): Int {
        return R.layout.chat_msg_from_row
    }
}

//klasa wiadomość do kogoś
class ChatMsgToItem(val text: String, val usr: User) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.msg_to.text = text
        Picasso.get().load(usr.photoUrl).into(viewHolder.itemView.msg_to_usr_pic)
    }

    override fun getLayout(): Int {
        return R.layout.chat_msg_to_row
    }
}

//klasa wątek
class MsgThread(val msg: Message) : Item<GroupieViewHolder>() {
    var userChattingWith: User? = null

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.thread_last_msg.text = msg.text
        val lastMsgUsrId: String = if (msg.fromId == FirebaseAuth.getInstance().uid) {
            msg.toId
        } else {
            msg.fromId
        }
        val ref = FirebaseDatabase.getInstance().getReference("/users/$lastMsgUsrId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userChattingWith = snapshot.getValue(User::class.java)
                viewHolder.itemView.thread_title.text = userChattingWith?.name
                Picasso.get().load(userChattingWith?.photoUrl)
                    .into(viewHolder.itemView.thread_picture)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun getLayout(): Int {
        return R.layout.threads_row
    }
}


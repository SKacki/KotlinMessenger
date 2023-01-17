package com.inzynierka.komunikat.classes

import com.inzynierka.komunikat.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_msg_from_row.view.*
import kotlinx.android.synthetic.main.chat_msg_to_row.view.*

//klasa wiadomość - ułatwia przesyłanie info do bazy danych
class message(val msgId : String, val fromId : String, val toId : String, val text : String, val timeStamp : Long)
{constructor() : this("","","","",0)
}


//klasa wiadomość od kogoś
class ChatMsgFromItem (val text : String, val usr : User) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.msg_from.text = text
        Picasso.get().load(usr.photoUrl).into(viewHolder.itemView.msg_from_usr_pic)
    }
    override fun getLayout(): Int {
        return R.layout.chat_msg_from_row
    }
}

//klasa wiadomość do kogoś
class ChatMsgToItem (val text : String, val usr : User) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.msg_to.text = text
        Picasso.get().load(usr.photoUrl).into(viewHolder.itemView.msg_to_usr_pic)
    }
    override fun getLayout(): Int {
        return R.layout.chat_msg_to_row
    }
}
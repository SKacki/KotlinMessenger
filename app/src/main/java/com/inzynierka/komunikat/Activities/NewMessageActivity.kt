package com.inzynierka.komunikat

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_new_message.new_message_recycler_view
import kotlinx.android.synthetic.main.user_row_new_message.view.*


class NewMessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = "Do kogo piszemy?"
        getUsers()
    }

    companion object //objekt do przekazania jako extra do następnej aktywności
    {
        val USER_KEY = "USER_KEY"

    }

private fun getUsers() {
    val ref = FirebaseDatabase.getInstance().getReference("/users")
    ref.addListenerForSingleValueEvent(object: ValueEventListener {

        override fun onDataChange(p0: DataSnapshot) {
            val adapter = GroupAdapter<GroupieViewHolder>()

            p0.children.forEach {
                val user = it.getValue(User::class.java)
                if (user != null) {
                    adapter.add(UserItem(user))
                }
            adapter.setOnItemClickListener{item, view ->

                val userItem = item as UserItem //trzeba rzutować na userItem, inaczej nie da się przekazać
                val intent = Intent(view.context, ChatActivity::class.java)
                intent.putExtra(USER_KEY, userItem.user) //przekazanie dodatkowych argumentów do intentu
                startActivity(intent)
                finish() }
                }
            new_message_recycler_view.adapter = adapter
            }

        override fun onCancelled(p0: DatabaseError) {}
        })
    }
}

class UserItem(val user:User) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.UserRow_userName.text=user.name
        Picasso.get().load(user.photoUrl).into(viewHolder.itemView.UserRow_avatar)
    }
    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }
}


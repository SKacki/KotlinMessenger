package com.inzynierka.komunikat.Activities


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.inzynierka.komunikat.R
import com.inzynierka.komunikat.classes.User
import com.inzynierka.komunikat.classes.UserItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_new_message.new_message_recycler_view

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
                    //TODO: do usunięcia z listy aktualnie zalogowany użytkownik
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

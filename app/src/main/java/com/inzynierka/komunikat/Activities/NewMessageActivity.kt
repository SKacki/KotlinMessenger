package com.inzynierka.komunikat.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.inzynierka.komunikat.R
import com.inzynierka.komunikat.classes.FriendsRequestState
import com.inzynierka.komunikat.classes.User
import com.inzynierka.komunikat.classes.UserItemGroupieViewHolder
import com.inzynierka.komunikat.utils.FirebaseUtils
import com.inzynierka.komunikat.utils.TOAST_USER_UID_NON_EXISTS
import com.inzynierka.komunikat.utils.makeToastShow
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_new_message.*

class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        supportActionBar?.title = "Do kogo piszemy?"

        FirebaseUtils.requireCurrentUser { currentUser ->
            if (currentUser == null) {
                makeToastShow(TOAST_USER_UID_NON_EXISTS)
            } else {
                observeFriendsList(currentUser)
            }
        }
    }

    private fun observeFriendsList(currentUser: User) {
        FirebaseUtils.observeFriendsList(
            uid = currentUser.uid,
            filterFriendsRequestState = FriendsRequestState.ACCEPTED
        ) { friendsList ->
            val adapter = GroupAdapter<GroupieViewHolder>()
            val friendListMapped = friendsList.map { user -> UserItemGroupieViewHolder(user) }

            adapter.addAll(friendListMapped)
            adapter.setOnItemClickListener { item: Item<GroupieViewHolder>, view: View ->

                val userItemGroupieViewHolder =
                    item as UserItemGroupieViewHolder // trzeba rzutować na userItem, inaczej nie da się przekazać

                val intent = Intent(view.context, ChatActivity::class.java).apply {
                    putExtra( //przekazanie dodatkowych argumentów do intentu
                        USER_KEY,
                        userItemGroupieViewHolder.user
                    )
                }

                startActivity(intent)
                finish()
            }

            new_message_recycler_view.adapter = adapter
        }
    }

    //obiekt do przekazania jako extra do następnej aktywności
    companion object {
        const val USER_KEY = "USER_KEY"
    }
}

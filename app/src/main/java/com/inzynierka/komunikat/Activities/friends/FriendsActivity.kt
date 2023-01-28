package com.inzynierka.komunikat.activities.friends

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.inzynierka.komunikat.R
import com.inzynierka.komunikat.classes.User
import com.inzynierka.komunikat.databinding.ActivityFriendsBinding
import com.inzynierka.komunikat.utils.*

class FriendsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFriendsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFriendsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Pobranie aktualnego użytkownika
        FirebaseUtils.requireCurrentUser { currentUser ->
            if (currentUser == null) {
                makeToastShow(TOAST_CURRENT_USER_UID_NON_EXISTS)
            } else {
                setupRecyclerView(currentUser)
                observerFriendsList(currentUser)
            }
        }
    }

    private fun setupRecyclerView(currentUser: User) {
        // Callback
        val onDeleteUser: (user: User) -> Unit = { friend ->
            FirebaseUtils.deleteFriend(currentUser, friend) { status, reason ->
                if (status) {
                    makeToastShow(TOAST_FRIEND_DELETED)
                } else {
                    makeToastShow(TOAST_FRIEND_DELETED_ERROR)
                    reason?.printStackTrace()
                }
            }
        }

        // Callback
        val onAcceptUser: (user: User) -> Unit = { friend ->
            // Aktualizacja znajomości w profilu obecnego użytkownika
            FirebaseUtils.acceptFriend(currentUser, friend) { status, reason ->
                if (status) {
                    makeToastShow(TOAST_FRIEND_ACCEPTED)
                } else {
                    makeToastShow(TOAST_FRIEND_ACCEPTED_ERROR)
                    reason?.printStackTrace()
                }
            }

            // Aktualizacja znajomości w profilu drugiego użytkownika
            FirebaseUtils.acceptFriend(friend, currentUser) { status, reason ->
                if (status) {
                    makeToastShow(TOAST_FRIEND_REQUEST_UPDATED)
                } else {
                    makeToastShow(TOAST_FRIEND_REQUEST_UPDATED_ERROR)
                    reason?.printStackTrace()
                }
            }
        }

        // Po kliknięciu usuń użytkownika
        binding.friendsRecyclerView.adapter = FriendsRowAdapter(
            onDeleteUser,
            onAcceptUser
        )
    }

    private fun observerFriendsList(currentUser: User) {
        FirebaseUtils.observeFriendsList(currentUser.uid) { friendList ->
            (binding.friendsRecyclerView.adapter as FriendsRowAdapter).setData(friendList)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.friends_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.goToAddFriend -> {
                startActivity(Intent(this, FriendsAddActivity::class.java))
                true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }
}
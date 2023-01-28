package com.inzynierka.komunikat.activities.friends

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.inzynierka.komunikat.R
import com.inzynierka.komunikat.classes.User
import com.inzynierka.komunikat.databinding.ActivityFriendsSearchBinding
import com.inzynierka.komunikat.utils.*


class FriendsInviteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFriendsSearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFriendsSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Pobranie aktualnego użytkownika
        FirebaseUtils.requireCurrentUser { currentUser ->
            if (currentUser == null) {
                makeToastShow(TOAST_CURRENT_USER_UID_NON_EXISTS)
            } else {
                setupRecyclerView()

                val searchUserName: String = intent.getStringExtra(SEARCH_PHRASE) ?: run {
                    throw IllegalArgumentException("Extra string \"$SEARCH_PHRASE\" not provided!")
                }

                getUserList(currentUser, searchUserName)
            }
        }
    }

    private fun setupRecyclerView() {
        // Callback
        val onInviteUserCallback: (user: User) -> Unit = { invitedUser ->
            setResult(
                Activity.RESULT_OK,
                Intent().putExtra(INVITED_USER, invitedUser)
            )
            finish()
        }

        binding.friendsRecyclerView.adapter = FriendsInviteRowAdapter(onInviteUserCallback)
    }

    private fun getUserList(currentUser: User, searchUserName: String) {
        FirebaseUtils.getUsers(
            listOf(
                FilterUsersByNameStartingWith(searchUserName),
                FilterOutUser(currentUser)
            )
        ) { friendList ->
            (binding.friendsRecyclerView.adapter as FriendsInviteRowAdapter).setData(friendList)
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

    companion object {
        private const val SEARCH_PHRASE = "SEARCH_PHRASE"
        private const val INVITED_USER = "INVITED USER"
    }
}
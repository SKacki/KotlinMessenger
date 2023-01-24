package com.inzynierka.komunikat.activities.friends

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.inzynierka.komunikat.R
import com.inzynierka.komunikat.classes.User
import com.inzynierka.komunikat.databinding.ActivityFriendsBinding
import com.inzynierka.komunikat.utils.FirebaseUtils

class FriendsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFriendsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFriendsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Pobranie aktualnego użytkownika
        FirebaseUtils.requireCurrentUser { currentUser ->
            setupRecyclerView(currentUser)
            observerFriendsList(currentUser)
        }
    }

    private fun setupRecyclerView(currentUser: User) {
        // Callback zwracający o użytkowniku który ma być usunięty"
        val onItemViewClickedCallback: (user: User) -> Unit = { friend ->
            FirebaseUtils.deleteUser(currentUser, friend) { status, reason ->
                if (status) {
                    Toast.makeText(
                        this,
                        "Twój znajomy ${friend.name} został usunięty",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "Twój znajomy ${friend.name} nie został usunięty, sprawdź logi",
                        Toast.LENGTH_SHORT
                    ).show()
                    reason?.printStackTrace()
                }
            }
        }

        // Po kliknięciu usuń użytkownika
        binding.friendsRecyclerView.adapter = FriendsRowAdapter(onItemViewClickedCallback)
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
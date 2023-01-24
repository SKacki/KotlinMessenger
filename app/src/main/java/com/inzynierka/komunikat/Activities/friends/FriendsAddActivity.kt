package com.inzynierka.komunikat.activities.friends

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.inzynierka.komunikat.classes.User
import com.inzynierka.komunikat.databinding.ActivityFriendsAddBinding
import com.inzynierka.komunikat.extensions.isEmptyOrBlank
import com.inzynierka.komunikat.utils.FirebaseUtils


class FriendsAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFriendsAddBinding

    private val friendUid
        get() = binding.friendsUid.text.toString()

    private val friendName
        get() = binding.friendsName.text.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFriendsAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.friendsAddBtn.setOnClickListener {
            if (friendName.isEmptyOrBlank() || friendUid.isEmptyOrBlank()) {
                Toast.makeText(this, "Oba pola muszą być uzupełnione!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Pobranie aktualnego użytkownika
            FirebaseUtils.requireCurrentUser { currentUser ->
                addFriend(currentUser, User(friendUid, friendName))
            }
        }
    }

    private fun addFriend(currentUser: User, friendUser: User) {
        // Callback zwracający informację czy operacją się udała czy też nie
        val callback: (status: Boolean, reason: Exception?) -> Unit = { status, reason ->
            if (status) {
                Toast.makeText(
                    this,
                    "Twój znajomy został dodany :-)",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            } else {
                Toast.makeText(
                    this,
                    "Nie udało się dodać użytkownika, sprawdź logi",
                    Toast.LENGTH_SHORT
                ).show()

                reason?.printStackTrace()
            }
        }

        // Dodanie nowego użytkownika jako znajomego dla aktualnego użytkownika
        FirebaseUtils.addFriendToUser(currentUser.uid, friendUser, callback)
    }
}
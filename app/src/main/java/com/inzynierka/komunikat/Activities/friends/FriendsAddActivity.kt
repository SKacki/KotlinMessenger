package com.inzynierka.komunikat.activities.friends

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.inzynierka.komunikat.classes.FriendsRequestState
import com.inzynierka.komunikat.classes.User
import com.inzynierka.komunikat.databinding.ActivityFriendsAddBinding
import com.inzynierka.komunikat.extensions.isEmptyOrBlank
import com.inzynierka.komunikat.utils.*


class FriendsAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFriendsAddBinding

    private val friendUid
        get() = binding.friendsUid.text.toString()

    private var currentUser2: User? = null
    private var requestedUser2: User? = null

    private val bothUsersExists = MutableLiveData(0)
    private val bothInvitationSent = MutableLiveData(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFriendsAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bothUsersExists.observe(this) {
            Log.i(TAG, "bothUsersExists = $it")

            if (it == 2) {
                onBothUsersExist()
            }
        }

        bothInvitationSent.observe(this) {
            if (it == 2) {
                Toast.makeText(
                    this,
                    "Zaproszenie wysłane :-)",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
        }

        binding.friendsAddBtn.setOnClickListener {
            // Reset wartości dla obserwatorów
            bothUsersExists.value = 0
            bothInvitationSent.value = 0

            // Walidacja
            if (friendUid.isEmptyOrBlank()) {
                makeToastShow(TOAST_BOTH_FIELDS_NEED_TO_BE_FILLED)
                return@setOnClickListener
            }

            // Stworzenie informacji o nowym użytkowniku
            val requestedUser = User(friendUid)

            // Sprawdzenie czy nowy użytkownik istnieje
            FirebaseUtils.requireUser(requestedUser) { user ->
                if (user != null) {
                    requestedUser2 = user
                    bothUsersExists.value = bothUsersExists.value?.plus(1)
                } else
                    makeToastShow(TOAST_USER_UID_NON_EXISTS)
            }

            // Sprawdzenie czy obecny użytkownik istnieje
            FirebaseUtils.requireCurrentUser { user ->
                if (user != null) {
                    currentUser2 = user
                    bothUsersExists.value = bothUsersExists.value?.plus(1)
                } else
                    makeToastShow(TOAST_CURRENT_USER_UID_NON_EXISTS)
            }
        }
    }

    private fun onBothUsersExist() {
        currentUser2?.let { currentUser ->
            requestedUser2?.let { requestedUser ->

                // dodanie zaproszenia dla obecnego użytkownika
                addFriend(
                    currentUser,
                    requestedUser.copy(
                        friendsRequestState = FriendsRequestState.SENT_AWAITING.state
                    )
                )

                // dodanie zaproszenie dla nowego użytkownika
                addFriend(
                    requestedUser,
                    currentUser.copy(
                        friendsRequestState = FriendsRequestState.RECEIVED_AWAITING.state
                    )
                )
            }
        }
    }

    private fun addFriend(currentUser: User, friendUser: User) {
        // Callback zwracający informację czy operacją się udała czy też nie
        val callback: (status: Boolean, reason: Exception?) -> Unit = { status, reason ->
            if (status) {
                bothInvitationSent.value = bothInvitationSent.value?.plus(1)
            } else {
                Toast.makeText(
                    this,
                    "Nie udało się wysłać zaproszenia dla użytkownika ${friendUser.name} ",
                    Toast.LENGTH_SHORT
                ).show()

                reason?.printStackTrace()
            }
        }

        // Dodanie nowego użytkownika jako znajomego dla aktualnego użytkownika
        FirebaseUtils.addFriendToUser(currentUser.uid, friendUser, callback)
    }

    companion object {
        const val TAG = "FriendsAddActivity"
    }
}

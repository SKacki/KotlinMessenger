package com.inzynierka.komunikat.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.inzynierka.komunikat.R
import com.inzynierka.komunikat.activities.friends.FriendsActivity
import com.inzynierka.komunikat.classes.User
import com.inzynierka.komunikat.utils.FirebaseUtils
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_profile.*


class ProfileActivity : AppCompatActivity() {

    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        //profil użytkownika z możliwością edycji danych

        FirebaseUtils.requireCurrentUser { currentUser ->
            Picasso.get().load(currentUser.photoUrl).into(profile_profile_picture)
            profile_user_name.text = currentUser.name
        }

        profile_firends_list_btn.setOnClickListener {
            startActivity(Intent(this, FriendsActivity::class.java))
        }

        //TODO: dodaj logikę
        profile_edit_profile_btn.setOnClickListener {
            Log.d("profileActivity", "Click!")
        }

        profile_delete_account_btn.setOnClickListener {
            deleteAccount()
        }
    }

    private fun deleteAccount() {
        val currentUser = FirebaseAuth.getInstance().currentUser!!
        val currentUserUid = FirebaseAuth.getInstance().uid

        FirebaseAuth.getInstance().signOut()

        val deletedUsrPhoto =
            "https://firebasestorage.googleapis.com/v0/b/komunikat-ccfa2.appspot.com/o/images%2Ff4e19f25-15d7-48a3-ad1e-4d15f5e3a3f5?alt=media&token=640d83cf-cbf8-405c-af11-3760a134b6a8"
        val deletedUser: User = User(currentUserUid!!, "Deleted User", deletedUsrPhoto)

        val userRef = FirebaseDatabase.getInstance().getReference("/users/$currentUserUid")
        val friendsRef = FirebaseDatabase.getInstance().getReference("/friends/$currentUserUid")
        val lastMsgRef =
            FirebaseDatabase.getInstance().getReference("/last_messages/$currentUserUid")
        val msgRef = FirebaseDatabase.getInstance().getReference("/messages/$currentUserUid")

        userRef.setValue(deletedUser)
        friendsRef.setValue(null)
        lastMsgRef.setValue(null)
        msgRef.setValue(null)

        currentUser.delete().addOnSuccessListener {
            Toast.makeText(this, "Twoje konto zostało usunięte. Żegnaj :(", Toast.LENGTH_LONG)
                .show()
        }

        val intent = Intent(this, RegisterActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)

    }
}
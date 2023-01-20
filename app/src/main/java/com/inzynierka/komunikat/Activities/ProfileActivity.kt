package com.inzynierka.komunikat.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.inzynierka.komunikat.R
import com.inzynierka.komunikat.classes.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_profile.*


class ProfileActivity : AppCompatActivity() {

    val adapter = GroupAdapter<GroupieViewHolder>()
    var user : User? = ThreadsActivity.user

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        //profil użytkownika z możliwością edycji danych

        Picasso.get().load(user?.photoUrl).into(profile_profile_picture)
        profile_user_name.text = user?.name

        //TODO: obsługa przycisków
        profile_firends_list_btn.setOnClickListener {
            Log.d("profileActivity","Click!")
        }
        profile_edit_profile_btn.setOnClickListener {
            Log.d("profileActivity","Click!")
        }
        profile_delete_account_btn.setOnClickListener {
            Log.d("profileActivity","Click!")
        }



    }
}
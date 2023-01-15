package com.inzynierka.komunikat

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.inzynierka.komunikat.Activities.ChatActivity
import com.inzynierka.komunikat.Activities.LoginActivity
import com.inzynierka.komunikat.Activities.ThreadsActivity
import com.inzynierka.komunikat.classes.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_new_message.new_message_recycler_view
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*
import java.util.*


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

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register_register_btn.setOnClickListener {
            //rejestracja
            registerUser()
        }

        register_redirect_to_login.setOnClickListener {
            //przekierowanie do logowania
            startActivity(Intent(this, LoginActivity::class.java))
        }

        //dodawanie zdjęcia
        register_photo.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }

    }

    //TODO: ustaw defaultowy avatar dla użytkownika który nie wybrał zdjęcia
    var photoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0 && resultCode == RESULT_OK && data != null){
            //ustawianie zdjęcia profilowego
            photoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, photoUri)
            register_selectphoto_imageview.setImageBitmap(bitmap)
            register_photo.alpha = 0f
        }
    }

    private fun registerUser(){

        val username = register_username.text.toString()
        val password = register_password.text.toString()
        val email = register_email.text.toString()

        //walidacja pól
        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Uzupełnij wymagane pola email/hasło ", Toast.LENGTH_SHORT).show()
            return
        }

        //TODO: jak zrobisz defaultowy avek, wywal ten blok
        if (photoUri == null){
            Toast.makeText(this, "Wybierz zdjęcie profilowe", Toast.LENGTH_SHORT).show()
            return
        }

        //dodanie nowego użytkownika do firebase
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(!it.isSuccessful){
                    return@addOnCompleteListener
                }
                val result = it.result
                if (result != null) {
                    if (result.user != null) {
                        Toast.makeText(this, "Witaj $username :)", Toast.LENGTH_SHORT).show()
                        uploadPhotoToFB()
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Ups. Coś poszło nie tak :(", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadPhotoToFB() {
        if (photoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(photoUri!!)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Pomyślnie wysłano plik: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    addUserToFB(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d("RegisterActivity", "Nie udało się wysłać pliku: ${it.message}")
            }
    }

    private fun addUserToFB(photoUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?:""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid, register_username.text.toString(), photoUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                //przejdz do profilu (wyczyść przedtem aktywności na stacku
                val intent = Intent(this, ThreadsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
    }

}
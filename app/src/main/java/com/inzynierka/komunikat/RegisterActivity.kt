package com.inzynierka.komunikat

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.UUID

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //rejestracja
        register_register_btn.setOnClickListener {
            registerUser()
        }

        //przekierowanie do logowania
        register_redirect_to_login.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        //dodawanie zdjęcia
        register_photo.setOnClickListener {
            Log.d("Main","click!")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }

    }

    var photoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            photoUri = data.data
            val bitmapPhoto = MediaStore.Images.Media.getBitmap(contentResolver, photoUri)
            val bitmapDrawable = BitmapDrawable(bitmapPhoto)
            register_photo.setBackgroundDrawable(bitmapDrawable)
            Log.d("Register", "hello photo: $photoUri")

        }
    }

    private fun registerUser(){

        val username = register_username.text.toString()
        val password = register_password.text.toString()
        val email = register_email.text.toString()

        //walidacja pól
        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Uzupełnij wymagane pola email/hasło", Toast.LENGTH_SHORT).show()
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
                    val user = result.user
                    if (user != null) {
                        Log.d("Main", "Sukces, uid nowego użytkownika: ${user.uid}")
                        Toast.makeText(this, "Witaj $username :)", Toast.LENGTH_SHORT).show()
                        uploadPhotoToFB()
                    }
                }
            }
            .addOnFailureListener {
                Log.d("Main", "Błąd: ${it.message}")
                Toast.makeText(this, "Ups. Coś poszło nie tak :(", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadPhotoToFB()
    {
        if(photoUri == null) return
        val ref = FirebaseStorage.getInstance().getReference("/images/" + UUID.randomUUID().toString())
        ref.putFile(photoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    addUserToFB(it.toString())
                }
            }
    }

    private fun addUserToFB(photoUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?:""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid,register_username.text.toString(), photoUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Pomyślnie zapisano użytkownika")
            }
    }

}

class User (val uid: String, val name: String, val photoUrl: String){

}
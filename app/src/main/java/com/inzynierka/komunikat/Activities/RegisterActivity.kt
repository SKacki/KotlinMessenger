package com.inzynierka.komunikat.Activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.inzynierka.komunikat.R
import com.inzynierka.komunikat.classes.User
import kotlinx.android.synthetic.main.activity_register.*
import java.util.UUID

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

    var photoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
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
        val user = User(uid,register_username.text.toString(), photoUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                //przejdz do profilu (wyczyść przedtem aktywności na stacku
                val intent = Intent(this, ThreadsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
    }

}

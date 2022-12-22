package com.inzynierka.komunikat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //rejestracja
        register_register_btn.setOnClickListener {
            registerUser()
        }

        //przekierowanie do logowania
        register_redirect_to_login.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
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
                    }
                }
            }
            .addOnFailureListener {
                Log.d("Main", "Błąd: ${it.message}")
                Toast.makeText(this, "Ups. Coś poszło nie tak :(", Toast.LENGTH_SHORT).show()
            }
    }
}
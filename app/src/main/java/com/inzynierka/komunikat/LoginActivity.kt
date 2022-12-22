package com.inzynierka.komunikat

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //logowanie
        login_login_btn.setOnClickListener {
            loginUser()
        }

        //przekierowanie do rejestracji
        login_redirect_to_register.setOnClickListener {
            finish()
        }

    }

    private fun loginUser(){
        val password = login_password.text.toString()
        val email = login_email.text.toString()

        Log.d("MainActivity", "Email: " + email + ", hasło: " + password)
        //tutaj dodaj logikę logowania
    }

}
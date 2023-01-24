package com.inzynierka.komunikat.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.inzynierka.komunikat.R
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

    private fun loginUser() {
        val password = login_password.text.toString()
        val email = login_email.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                this,
                "Uzupełnij wymagane pola email/hasło.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    return@addOnCompleteListener
                }
                startActivity(Intent(this, ThreadsActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "Ups, coś poszło nie tak: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}
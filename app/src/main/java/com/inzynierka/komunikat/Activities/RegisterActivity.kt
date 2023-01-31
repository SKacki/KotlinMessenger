package com.inzynierka.komunikat.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.inzynierka.komunikat.R
import com.inzynierka.komunikat.classes.User
import com.inzynierka.komunikat.utils.FirebaseUtils
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register_register_btn.setOnClickListener {
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
            startActivityForResult(intent, 0)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            //ustawianie zdjęcia profilowego
            photoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, photoUri)
            register_selectphoto_imageview.setImageBitmap(bitmap)
            register_photo.alpha = 0f
        }
    }

    private fun registerUser() {
        val username = register_username.text.toString()
        val password = register_password.text.toString()
        val email = register_email.text.toString()

        //walidacja pól
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Uzupełnij wymagane pola email/hasło", Toast.LENGTH_SHORT).show()
            return
        }

        //dodanie nowego użytkownika do firebase

        // listener w sytuacji gdy autoryzacja się powiodła
        val onCompleteLister: (Task<AuthResult>) -> Unit = { taskAuthResult: Task<AuthResult> ->
            if (!taskAuthResult.isSuccessful) {
                Toast.makeText(
                    this@RegisterActivity,
                    "Nie można zarejestrować użytkownika, sprawdź logi",
                    Toast.LENGTH_SHORT
                ).show()

                taskAuthResult.exception?.printStackTrace()
            } else {

                // uzyskanie informacji o koncie
                taskAuthResult.result.user?.let { firebaseUser ->
                    Toast.makeText(this, "Witaj $username :)", Toast.LENGTH_SHORT).show()

                    // przed dodaniem użytkownika, spróbuj przygotować zdjęcie

                    photoUri?.let { photoUri ->

                        // pobranie URL do obrazka
                        uploadUserImage(photoUri, firebaseUser.uid) { photoUrl: String? ->

                            // dodanie użytkownika ze zdjęciem lub bez
                            addUserToFirebase(
                                User(firebaseUser.uid, username, photoUrl ?: User.DEFAULT_PHOTO)
                            )
                        }
                    } ?: kotlin.run {

                        // dodanie użytkownika bez zdjęcia
                        addUserToFirebase(
                            User(firebaseUser.uid, username)
                        )
                    }
                }
            }
        }

        // listener w sytuacji gdy autoryzacja się nie powiodła
        val onFailureListener: (Exception) -> Unit = {
            Toast.makeText(this, "Ups. Coś poszło nie tak :(", Toast.LENGTH_SHORT).show()
            it.printStackTrace()
        }

        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(onCompleteLister)
            .addOnFailureListener(onFailureListener)
    }

    private fun addUserToFirebase(user: User) {
        FirebaseUtils.addUserToFirebase(user) { result, reason ->

            // poprawne dodanie użytkownika
            if (result) {
                startActivity(Intent(this, ThreadsActivity::class.java))
                finish()
            } else { // niepoprawne dodanie użytkownika

                // komunikat dla użytkownika
                Toast.makeText(
                    this,
                    "Nie udało się poprawnie stworzyć konta, sprawdź logi",
                    Toast.LENGTH_SHORT
                ).show()

                // komunikat dla developera
                reason?.printStackTrace()
            }
        }
    }

    private fun uploadUserImage(photoUri: Uri, uid: String, callback: (String?) -> Unit) {
        FirebaseUtils.uploadImage(uid, photoUri) { photoUrl ->
            callback.invoke(photoUrl)
        }
    }

    companion object {
        private const val TAG = "RegisterActivity"
    }
}

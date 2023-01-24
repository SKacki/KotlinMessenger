package com.inzynierka.komunikat.utils

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.inzynierka.komunikat.classes.User
import com.inzynierka.komunikat.listeners.SimpleValueEventListener


object FirebaseUtils {

    /**
     * Funkcja przyjmuje jako argument funkcję zwrotną, która zostanie wywołana z danymi aktualnego użytkownika.
     * Pobiera ona instancję FirebaseDatabase i UID aktualnego użytkownika z FirebaseAuth.
     * Następnie pobiera referencję do danych użytkownika w FirebaseDatabase za pomocą UID.
     * Funkcja ustawia nasłuch na pojedynczą wartość zdarzenia dla referencji,
     * tak aby pobrać dane użytkownika tylko raz i wywołać funkcję zwrotną z danymi.
     * W przypadku jakiegokolwiek błędu, komunikat błędu jest zwracany.
     *
     * @param callback funkcja zwrotna, która przyjmuje obiekt typu User jako argument
     */
    fun requireCurrentUser(callback: (currentUser: User) -> Unit) {
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val uid = FirebaseAuth.getInstance().uid
        val ref = firebaseDatabase.getReference("/users/$uid")

        ref.addListenerForSingleValueEvent(object : SimpleValueEventListener() {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue(User::class.java)
                value?.let(callback)
            }

            override fun onCancelled(error: DatabaseError) {
                super.onCancelled(error)
                Log.e("requireCurrentUser", "onCancelled: $error")
            }
        })
    }

    /**
     * Obserwuje listę znajomych użytkownika w bazie danych Firebase.
     *
     * Funkcja ustanawia połączenie z bazą danych Firebase oraz ustawia referencję
     * do odpowiedniego węzła w drzewie bazy danych.
     * Dodatkowo dodaje nasłuchiwacz, który reaguje na zmiany w danych pod referencją.
     * W przypadku zmiany danych, funkcja tworzy listę znajomych użytkownika i wywołuje przekazaną
     * funkcję callback z tą listą jako argumentem.
     *
     * @param uid Unikalny identyfikator użytkownika, dla którego ma być obserwowana lista znajomych.
     * @param callback Funkcja wywoływana zwrotnie, która przyjmuje jako
     * argument listę obiektów [User] reprezentujących znajomych użytkownika.
     */
    fun observeFriendsList(uid: String, callback: (List<User>) -> Unit) {
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val ref = firebaseDatabase.getReference("/users/$uid/friends/")

        ref.addValueEventListener(object : SimpleValueEventListener() {
            override fun onDataChange(snapshot: DataSnapshot) {
                val friendList = mutableListOf<User>()

                for (postSnapshot in snapshot.children) {
                    postSnapshot.getValue(User::class.java)?.let { user ->
                        friendList.add(user)
                    }
                }

                callback.invoke(friendList)
            }
        })
    }

    /**
     * Pobiera listę znajomych dla danego użytkownika z Firebase Database.
     *
     * @param uid ID użytkownika, dla którego ma zostać pobrana lista znajomych.
     * @param callback Funkcja zwrotna, która zostanie wywołana z listą znajomych.
     */
    fun getFriendsList(uid: String, callback: (List<User>) -> Unit) {
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val ref = firebaseDatabase.getReference("/users/$uid/friends/")

        ref.addListenerForSingleValueEvent(object : SimpleValueEventListener() {
            override fun onDataChange(snapshot: DataSnapshot) {
                val friendList = mutableListOf<User>()

                for (postSnapshot in snapshot.children) {
                    postSnapshot.getValue(User::class.java)?.let { user ->
                        friendList.add(user)
                    }
                }

                callback.invoke(friendList)
            }
        })
    }

    /**
     * Funkcja odpowiedzialna za upload obrazka na Firebase Storage
     * @param uid - unikalny identyfikator użytkownika
     * @param uri - adres obrazu w urządzeniu
     * @param callback - funkcja callback zwracająca url obrazu po jego wgraniu
     */
    fun uploadImage(uid: String, uri: Uri, callback: (imageUrl: String?) -> Unit) {
        val firebaseStorage = FirebaseStorage.getInstance()
        val ref = firebaseStorage.getReference("/images/$uid/avatar")

        val onFailureListener: (exception: Exception) -> Unit = {
            Log.d("RegisterActivity", "Nie udało się wysłać pliku: ${it.message}")
            callback.invoke(null)
        }

        val onSuccessListener: (UploadTask.TaskSnapshot) -> Unit =
            { uploadTask: UploadTask.TaskSnapshot ->
                Log.d("RegisterActivity", "Pomyślnie wysłano plik: ${uploadTask.metadata?.path}")
                ref.downloadUrl
                    .addOnSuccessListener { callback.invoke(it.toString()) }
                    .addOnFailureListener(onFailureListener)
            }

        ref.putFile(uri)
            .addOnSuccessListener(onSuccessListener)
            .addOnFailureListener(onFailureListener)
    }

    /**
     * Funkcja dodająca przyjaciela do użytkownika
     * @param uid - unikalny identyfikator użytkownika, do którego ma zostać dodany przyjaciel
     * @param user - obiekt użytkownika, który ma zostać dodany jako przyjaciel
     * @param callback - funkcja callback zwracająca status dodania przyjaciela
     * oraz powód niepowodzenia (jeśli takie wystąpiło)
     */
    fun addFriendToUser(
        uid: String,
        user: User,
        callback: (status: Boolean, reason: java.lang.Exception?) -> Unit,
    ) {
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val ref = firebaseDatabase.getReference("/users/$uid/friends/${user.uid}")

        ref.setValue(user)
            .addOnSuccessListener {
                callback.invoke(true, null)
            }
            .addOnFailureListener {
                callback.invoke(false, it)
            }
    }

    /**
     * Funkcja służąca do dodawania użytkownika do bazy danych Firebase
     * @param user - obiekt użytkownika do dodania
     * @param callback - funkcja callback informująca o powodzeniu
     * lub niepowodzeniu operacji oraz ewentualnym powodzie niepowodzenia
     */
    fun addUserToFirebase(
        user: User,
        callback: (status: Boolean, reason: java.lang.Exception?) -> Unit,
    ) {
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val ref = firebaseDatabase.getReference("/users/${user.uid}")

        ref.setValue(user)
            .addOnSuccessListener {
                callback.invoke(true, null)
            }
            .addOnFailureListener {
                callback.invoke(false, it)
            }
    }

    /**
     * Usuwa określonego użytkownika z listy znajomych bieżącego użytkownika.
     *
     * @param currentUser Bieżący użytkownik.
     * @param otherUser Użytkownik, który ma zostać usunięty z listy znajomych bieżącego użytkownika.
     * @param callback Funkcja zwrotna, która jest wywoływana z wartością logiczną
     * oznaczającą pomyślność usunięcia oraz obiektem java.lang.Exception zawierającym
     * powód niepowodzenia, jeśli takie istnieje.
     */
    fun deleteUser(
        currentUser: User,
        otherUser: User,
        callback: (status: Boolean, reason: java.lang.Exception?) -> Unit,
    ) {
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val ref =
            firebaseDatabase.getReference("/users/${currentUser.uid}/friends/${otherUser.uid}")

        ref.removeValue()
            .addOnSuccessListener {
                callback.invoke(true, null)
            }
            .addOnFailureListener {
                callback.invoke(false, it)
            }
    }
}

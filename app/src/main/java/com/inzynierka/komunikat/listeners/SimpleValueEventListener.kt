package com.inzynierka.komunikat.listeners

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

/**
 * Klasa opakowująca [ValueEventListener].
 * Zmniejsza ilość kodu poprzez nadpisywanie tylko wybranej funkcji.
 */
open class SimpleValueEventListener : ValueEventListener {
    override fun onDataChange(snapshot: DataSnapshot) {}
    override fun onCancelled(error: DatabaseError) {}
}

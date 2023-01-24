package com.inzynierka.komunikat.listeners

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

/**
 * Klasa opakowująca [ChildEventListener].
 * Zmniejsza ilość kodu poprzez nadpisywanie tylko wybranej funkcji.
 */
open class SimpleChildEventListener : ChildEventListener {
    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {}
    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
    override fun onChildRemoved(snapshot: DataSnapshot) {}
    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
    override fun onCancelled(error: DatabaseError) {}
}

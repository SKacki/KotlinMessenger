package com.inzynierka.komunikat.utils

import android.content.Context
import android.widget.Toast

fun Context.makeToastShow(msg: String) {
    Toast.makeText(
        this,
        msg,
        Toast.LENGTH_SHORT
    ).show()
}

const val TOAST_CURRENT_USER_UID_NON_EXISTS = "Problem z pobraniem informacji o obecnym użytkowniku"
const val TOAST_USER_UID_NON_EXISTS = "Użytkownik o takim UID nie istnieje!"
const val TOAST_BOTH_FIELDS_NEED_TO_BE_FILLED = "Oba pola muszą być uzupełnione!"
const val TOAST_FRIEND_DELETED = "Twój znajomy został usunięty"
const val TOAST_FRIEND_DELETED_ERROR = "Twój znajomy nie został usunięty, sprawdź logi"
const val TOAST_FRIEND_ACCEPTED = "Zaakceptowano zaproszenie"
const val TOAST_FRIEND_ACCEPTED_ERROR = "Akceptacja zaproszenia z błędem, sprawdź logi"
const val TOAST_FRIEND_REQUEST_UPDATED = "Twój znajomy został poinformowany"
const val TOAST_FRIEND_REQUEST_UPDATED_ERROR = "Twój znajomy nie został poinformowany z powodu błędu, sprawdź logi"
const val TOAST_PERMISSIONS_NOT_ALL_GRANTED = "Jedno z uprawnień nie zostało zatwierdzone. Zamykanie aplikacji..."


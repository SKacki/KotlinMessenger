package com.inzynierka.komunikat.classes
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User (val uid: String, val name: String, val photoUrl: String) : Parcelable {
    //bezargumentowy konstruktor, do fetchowania użytkowników z fb
    constructor() : this("","","")

}
package com.inzynierka.komunikat.classes
import android.os.Parcelable
import com.inzynierka.komunikat.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.user_row_new_message.view.*

@Parcelize
class User (val uid: String, val name: String, val photoUrl: String) : Parcelable {
    //bezargumentowy konstruktor, do fetchowania użytkowników z fb
    constructor() : this("","","")

}

class UserItem(val user:User) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.UserRow_userName.text=user.name
        Picasso.get().load(user.photoUrl).into(viewHolder.itemView.UserRow_avatar)
    }
    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }
}
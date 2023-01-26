package com.inzynierka.komunikat.classes

import android.os.Parcelable
import com.inzynierka.komunikat.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.user_row_new_message.view.*

@Parcelize
data class User(
    val uid: String = "",
    val name: String = "",
    val photoUrl: String = DEFAULT_PHOTO,
    val friends: Map<String, User> = mapOf(),
    val friendsRequestState: Int = 0,
) : Parcelable {

    companion object {
        const val DEFAULT_PHOTO =
            "https://firebasestorage.googleapis.com/v0/b/komunikat-ccfa2.appspot.com/o/images%2Ff4e19f25-15d7-48a3-ad1e-4d15f5e3a3f5?alt=media&token=640d83cf-cbf8-405c-af11-3760a134b6a8"
    }
}

enum class FriendsRequestState(val state: Int) {
    OFF(0),
    SENT_AWAITING(1),
    RECEIVED_AWAITING(2),
    ACCEPTED(3);

    companion object {
        fun toState(value: Int): FriendsRequestState {
            return when (value) {
                0 -> OFF
                1 -> SENT_AWAITING
                2 -> RECEIVED_AWAITING
                3 -> ACCEPTED
                else -> throw IllegalArgumentException("Unknown value state, was $value")
            }
        }
    }
}

class UserItemGroupieViewHolder(val user: User) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.UserRow_userName.text = user.name
        Picasso.get().load(user.photoUrl).into(viewHolder.itemView.UserRow_avatar)
    }

    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }
}

@Deprecated("Please use User class")
data class Friend(
    val friendUid: String,
    val friendName: String,
)
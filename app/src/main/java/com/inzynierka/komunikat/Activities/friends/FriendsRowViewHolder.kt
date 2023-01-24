package com.inzynierka.komunikat.activities.friends

import androidx.recyclerview.widget.RecyclerView
import com.inzynierka.komunikat.classes.User
import com.inzynierka.komunikat.databinding.FriendsRowBinding
import com.squareup.picasso.Picasso

class FriendsRowViewHolder(
    private val binding: FriendsRowBinding,
    private val callback: (user: User) -> Unit,
) :
    RecyclerView.ViewHolder(binding.root) {
    fun onBind(user: User) = with(binding) {
        friendName.text = user.name
        friendsUid.text = user.uid

        Picasso.get().load(user.photoUrl).into(binding.friendsImage)

        friendsDelBtn.setOnClickListener {
            callback.invoke(user)
        }
    }
}

package com.inzynierka.komunikat.activities.friends

import androidx.recyclerview.widget.RecyclerView
import com.inzynierka.komunikat.classes.User
import com.inzynierka.komunikat.databinding.FriendsInviteRowBinding
import com.squareup.picasso.Picasso

class FriendsInviteRowViewHolder(
    private val binding: FriendsInviteRowBinding,
    private val callbackInvite: ((user: User) -> Unit),
) :
    RecyclerView.ViewHolder(binding.root) {
    fun onBind(user: User) = with(binding) {
        inviteFriendName.text = user.name
        inviteFriendsUid.text = user.uid

        Picasso.get().load(user.photoUrl).into(binding.inviteFriendsImage)

        inviteBtn.setOnClickListener {
            callbackInvite.invoke(user)
        }
    }
}

package com.inzynierka.komunikat.activities.friends

import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.inzynierka.komunikat.classes.FriendsRequestState
import com.inzynierka.komunikat.classes.User
import com.inzynierka.komunikat.databinding.FriendsRowBinding
import com.squareup.picasso.Picasso

class FriendsRowViewHolder(
    private val binding: FriendsRowBinding,
    private val callbackDelete: (user: User) -> Unit,
    private val callbackAccept: (user: User) -> Unit,
) :
    RecyclerView.ViewHolder(binding.root) {
    fun onBind(user: User) = with(binding) {
        inviteFriendName.text = user.name
        inviteFriendsDescription.text = user.description

        Picasso.get().load(user.photoUrl).into(binding.inviteFriendsImage)

        when (FriendsRequestState.toState(user.friendsRequestState)) {
            FriendsRequestState.OFF -> {
                friendsDelBtn.isInvisible = false
                friendsAcceptBtn.isInvisible = true
                friendsInvitationSentTv.isInvisible = true
            }
            FriendsRequestState.SENT_AWAITING -> {
                friendsDelBtn.isInvisible = true
                friendsAcceptBtn.isInvisible = true
                friendsInvitationSentTv.isInvisible = false
            }
            FriendsRequestState.RECEIVED_AWAITING -> {
                friendsDelBtn.isInvisible = false
                friendsAcceptBtn.isInvisible = false
                friendsInvitationSentTv.isInvisible = true
            }
            FriendsRequestState.ACCEPTED -> {
                friendsDelBtn.isInvisible = false
                friendsAcceptBtn.isInvisible = true
                friendsInvitationSentTv.isInvisible = true
            }
        }

        friendsDelBtn.setOnClickListener {
            callbackDelete.invoke(user)
        }

        friendsAcceptBtn.setOnClickListener {
            callbackAccept.invoke(user)
        }
    }
}

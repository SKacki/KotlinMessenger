package com.inzynierka.komunikat.activities.friends

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.inzynierka.komunikat.classes.User
import com.inzynierka.komunikat.databinding.FriendsInviteRowBinding

class FriendsInviteRowAdapter(
    private val callbackInvite: (user: User) -> Unit,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var adapterFriendList = mutableListOf<User>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val friendsInviteRowBinding = FriendsInviteRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FriendsInviteRowViewHolder(
            friendsInviteRowBinding,
            callbackInvite,
        )
    }

    override fun getItemCount(): Int {
        return adapterFriendList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as FriendsInviteRowViewHolder).onBind(adapterFriendList[position])
    }

    fun setData(friendList: List<User>?) {
        adapterFriendList.clear()
        friendList?.let { adapterFriendList.addAll(it) }
        notifyDataSetChanged()
    }

}

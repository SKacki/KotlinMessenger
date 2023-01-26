package com.inzynierka.komunikat.activities.friends

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.inzynierka.komunikat.classes.User
import com.inzynierka.komunikat.databinding.FriendsRowBinding

class FriendsRowAdapter(
    private val callbackDelete: (user: User) -> Unit,
    private val callbackAccept: (user: User) -> Unit,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var adapterFriendList = mutableListOf<User>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val friendsRowBinding = FriendsRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FriendsRowViewHolder(
            friendsRowBinding,
            callbackDelete,
            callbackAccept
        )
    }

    override fun getItemCount(): Int {
        return adapterFriendList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as FriendsRowViewHolder).onBind(adapterFriendList[position])
    }

    fun setData(friendList: List<User>?) {
        adapterFriendList.clear()
        friendList?.let { adapterFriendList.addAll(it) }
        notifyDataSetChanged()
    }

}

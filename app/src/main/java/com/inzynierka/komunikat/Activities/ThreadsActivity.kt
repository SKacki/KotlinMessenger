package com.inzynierka.komunikat.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.inzynierka.komunikat.Activities.NewMessageActivity.Companion.USER_KEY
import com.inzynierka.komunikat.R
import com.inzynierka.komunikat.classes.Message
import com.inzynierka.komunikat.classes.MsgThread
import com.inzynierka.komunikat.classes.User
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_threads.*


class ThreadsActivity : AppCompatActivity() {

    companion object{
        var user : User? = null

    }
    val adapter = GroupAdapter<GroupieViewHolder>()
    val lastMsgMap = HashMap<String, Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_threads)

        //pamiętaj żeby to wywalić
        val tst_msg : Message = Message("-NM0yZLbpIHIpgYwdD54","zCVESgpmDDWT4yq2bjSxSclHrNZ2","AZMTFDsm8HNU5rG2HpSLQ7VzI2k2","Hey Chad",1673996685281)
        adapter.add(MsgThread(tst_msg))
        adapter.add(MsgThread(tst_msg))
        //

        threads_recycler_view.adapter = adapter
        threads_recycler_view.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
        adapter.setOnItemClickListener{ item, view ->
            val intent = Intent(this,ChatActivity::class.java)
            intent.putExtra(USER_KEY, (item as MsgThread).userChattingWith)
            startActivity(intent)
        }

        getCurrentUser()
        verifyIfUserIsLoggedIn()
        //updateLatestMessage() //TODO: gdzieś w tej funkcji jest nullpointer
    }

    private fun verifyIfUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //switch po itemach w menu
        when (item?.itemId) {
            R.id.menu_new_msg -> {
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.navigation_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun getCurrentUser(){
        val ref = FirebaseDatabase.getInstance().getReference("/users/${FirebaseAuth.getInstance().uid}")
        ref.addListenerForSingleValueEvent (object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(User::class.java)
            }
            //tutaj nic nie nadpisuję
            override fun onCancelled(error: DatabaseError) {}
        })


    }

    private fun updateLatestMessage(){
        val usrUid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/last_messages/$usrUid")
        ref.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java) ?: return
                lastMsgMap[snapshot.key!!] = message
                updateRecyclerView()
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                //val message = snapshot.getValue(Message::class.java) ?: return
                //lastMsgMap[snapshot.key!!] = message
                //updateRecyclerView()
            }
            override fun onCancelled(error: DatabaseError) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
        })
    }

    private fun updateRecyclerView() {
        adapter.clear()
        lastMsgMap.values.forEach { adapter.add(MsgThread(it)) }
    }

}
package com.example.kotlinchatmessenger.ui

import android.content.Intent
import android.icu.lang.UCharacter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.kotlinchatmessenger.R
import com.example.kotlinchatmessenger.items.LatestMessageItem
import com.example.kotlinchatmessenger.model.ChatMessages
import com.example.kotlinchatmessenger.model.User
import com.example.kotlinchatmessenger.ui.NewUser.Companion.USER_KEY
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.activity_latest_messages.*
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessagesActivity : AppCompatActivity() {

    companion object{
        var currentUser:User? = null
    }
    var userId:String?=null
    val latestMessageAdapter = GroupAdapter<GroupieViewHolder>()
    val latestMessageHashMap = HashMap<String,ChatMessages>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)
        userId = FirebaseAuth.getInstance().uid
        verifyLoginOrNot()

        getCurrentUserDetails()


        getLatestMessages()
    }

    private fun getLatestMessages() {

        rv_latest_messages.adapter = latestMessageAdapter
        rv_latest_messages.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
        latestMessageAdapter.setOnItemClickListener { item, view ->
            val user = item as LatestMessageItem
            val intent = Intent(this,ChatLogActivity::class.java)
            intent.putExtra(USER_KEY,user.chatPartnerUser)
            startActivity(intent)
        }
        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/LatestMessage/$userId")
        latestMessageRef.addChildEventListener(object:ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                latestMessageHashMap[p0.key!!] = p0.getValue(ChatMessages::class.java)!!
                refreshRecyclerView()
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                latestMessageHashMap[p0.key!!] = p0.getValue(ChatMessages::class.java)!!
                refreshRecyclerView()
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }

    private fun refreshRecyclerView(){
        latestMessageAdapter.clear()
        latestMessageHashMap.forEach{
            latestMessageAdapter.add(LatestMessageItem(it.value))
        }

    }

    private fun getCurrentUserDetails() {

        val ref = FirebaseDatabase.getInstance().getReference("/Users/$userId")
        ref.addListenerForSingleValueEvent(object:ValueEventListener{

            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
            }
            override fun onCancelled(p0: DatabaseError) {

            }

        })
    }

    private fun verifyLoginOrNot() {
        val uid = FirebaseAuth.getInstance().uid
        if(uid == null)
        {
            val intent = Intent(this,LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_sign_out ->{
                FirebaseAuth.getInstance().signOut()
                val intent= Intent(this,LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            R.id.menu_new_message ->{
                val intent = Intent(this,NewUser::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }


}

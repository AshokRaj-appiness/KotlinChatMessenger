package com.example.kotlinchatmessenger.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.kotlinchatmessenger.R
import com.example.kotlinchatmessenger.model.ChatMessages
import com.example.kotlinchatmessenger.model.User
import com.example.kotlinchatmessenger.ui.NewUser.Companion.USER_KEY
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_log_from_message.*
import kotlinx.android.synthetic.main.chat_log_from_message.view.*
import kotlinx.android.synthetic.main.chat_log_to_message.view.*

class ChatLogActivity : AppCompatActivity() {
    lateinit var user: User;
    lateinit var adapter:GroupAdapter<GroupieViewHolder>;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)


        if(intent.getParcelableExtra<User>(USER_KEY)!=null){
            user = intent.getParcelableExtra<User>(USER_KEY)
        }
        supportActionBar?.title = user.userName
        button.setOnClickListener {
            performSendMessage(editText.text.toString())
        }
        adapter = GroupAdapter<GroupieViewHolder>()
        populateData()

    }

    private fun performSendMessage(messageText:String){
        val fromId = FirebaseAuth.getInstance().uid
        val toId = user.userId

        val reference = FirebaseDatabase.getInstance().getReference("/User-messages/$fromId/$toId").push()
        val referenceFromAnotheEnd = FirebaseDatabase.getInstance().getReference("/User-messages/$toId/$fromId").push()
        val chatMessage = ChatMessages(fromId!!,reference.key!!,messageText,System.currentTimeMillis()/1000,toId)
        reference.setValue(chatMessage).addOnSuccessListener {
            editText.text.clear()
            rv_messages.scrollToPosition(adapter.itemCount-1)
        }
        referenceFromAnotheEnd.setValue(chatMessage)

        val latestMessageRefFrom = FirebaseDatabase.getInstance().getReference("/LatestMessage/$fromId/$toId")
        val latestMessageRefTo = FirebaseDatabase.getInstance().getReference("/LatestMessage/$toId/$fromId")
        latestMessageRefFrom.setValue(chatMessage)
        latestMessageRefTo.setValue(chatMessage)
    }

    private fun populateData(){
       val fromId = FirebaseAuth.getInstance().uid
       val toId = user.userId
       val ref = FirebaseDatabase.getInstance().getReference("/User-messages/$fromId/$toId")
        ref.addChildEventListener(object:ChildEventListener{

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessages::class.java)
                val fromIdHasToBe = FirebaseAuth.getInstance().uid
                if(chatMessage!=null){
                    if(chatMessage.fromId.equals(fromIdHasToBe))
                        adapter.add(ChatFrom(chatMessage.text,LatestMessagesActivity.currentUser!!))
                    else
                        adapter.add(ChatTo(chatMessage.text,user))
                }
                rv_messages.adapter = adapter
                rv_messages.scrollToPosition(adapter.itemCount-1)
            }
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }



        })
    }
}
class ChatFrom(val text:String,val user:User): com.xwray.groupie.kotlinandroidextensions.Item(){
    override fun bind(viewHolder: com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder, position: Int) {
        viewHolder.itemView.tv_message.text = text
        Picasso.get().load(user.imageUrl).into(viewHolder.itemView.iv_from_img)

    }

    override fun getLayout(): Int{
        return R.layout.chat_log_from_message
    }



}
class ChatTo(val text:String,val touser: User):com.xwray.groupie.kotlinandroidextensions.Item(){
    override fun bind(viewHolder: com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView.text = text
        val imageUrl = touser.imageUrl
        Picasso.get().load(imageUrl).into(viewHolder.itemView.iv_toImage)
    }

    override fun getLayout(): Int {
        return R.layout.chat_log_to_message
    }



}
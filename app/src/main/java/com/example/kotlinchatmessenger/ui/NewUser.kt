package com.example.kotlinchatmessenger.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinchatmessenger.R
import com.example.kotlinchatmessenger.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_new_user.*
import kotlinx.android.synthetic.main.new_user_item.view.*

class NewUser : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_user)
        supportActionBar?.title = "Select User"
        fetchUsers()
    }

    private fun fetchUsers() {
        val dbRef = FirebaseDatabase.getInstance().getReference("/Users")
        dbRef.addListenerForSingleValueEvent(object :ValueEventListener{
            var adapter = GroupAdapter<GroupieViewHolder>()


            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    val user = it.getValue(User::class.java)
                    if(user !=null){
                        if(user.userId != FirebaseAuth.getInstance().uid)
                        adapter.add(UserItem(user))
                    }

                }
                adapter.setOnItemClickListener { item, view ->
                    val item1 = item as UserItem
                    val intent = Intent(view.context,ChatLogActivity::class.java)
                    intent.putExtra(USER_KEY,item1.user)
                    startActivity(intent)
                    finish()
                }
                rv_users.adapter = adapter
                rv_users.addItemDecoration(DividerItemDecoration(this@NewUser,DividerItemDecoration.VERTICAL))
            }

        })

    }
    companion object{
        val USER_KEY = "userKey"
    }

}
class UserItem(val user:User):com.xwray.groupie.kotlinandroidextensions.Item(){
    override fun bind(viewHolder: com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder, position: Int) {
        viewHolder.itemView.tv_username.text = user.userName
        Picasso.get().load(user.imageUrl).into(viewHolder.itemView.iv_user_icon);
    }


    override fun getLayout(): Int {
        return R.layout.new_user_item
    }

}


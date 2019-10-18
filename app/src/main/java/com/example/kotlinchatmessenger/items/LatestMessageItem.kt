package com.example.kotlinchatmessenger.items

import android.util.Log
import com.example.kotlinchatmessenger.R
import com.example.kotlinchatmessenger.model.ChatMessages
import com.example.kotlinchatmessenger.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessageItem(val chatMessages: ChatMessages): Item(){
    var chatPartnerUser:User?=null
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            var chatPartnerId:String?=null
            if(chatMessages.fromId == FirebaseAuth.getInstance().uid)
                chatPartnerId = chatMessages.toId
            else
                chatPartnerId = chatMessages.fromId

            Log.d("==>","$chatPartnerId")
            val ref = FirebaseDatabase.getInstance().getReference("/Users/$chatPartnerId")
            ref.addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    chatPartnerUser = p0.getValue(User::class.java)
                    viewHolder.itemView.tv_username.text = chatPartnerUser?.userName
                    Picasso.get().load(chatPartnerUser?.imageUrl).into(viewHolder.itemView.imageView)

                }

            })
            viewHolder.itemView.tv_latest_messages.text = chatMessages.text
        }

        override fun getLayout(): Int {
            return R.layout.latest_message_row
        }

    }
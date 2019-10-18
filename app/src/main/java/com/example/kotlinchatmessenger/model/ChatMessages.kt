package com.example.kotlinchatmessenger.model

class ChatMessages(val fromId:String,val id:String,val text:String,val timeStamp:Long,val toId:String){
    constructor():this("","","",0,"")
}
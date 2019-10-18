package com.example.kotlinchatmessenger.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
    class User(val userId:String,val userName:String,val imageUrl:String): Parcelable {
        constructor():this("","","")
    }
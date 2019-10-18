package com.example.kotlinchatmessenger.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth

class SignUpLoginViewModel(application: Application): AndroidViewModel(application) {
   lateinit var application1:Application;
   var signupResult:MutableLiveData<String> = MutableLiveData<String>()

   init {
       application1 = application
   }

   fun signUpNewUserWithEmailAndPassword(email:String,password:String){
         FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
               if(!it.isSuccessful) return@addOnCompleteListener
               else signupResult.value = "User created with uid: ${it.result?.user?.uid}"
            }
            .addOnFailureListener {
                signupResult.value = it.message
            }
   }

   fun getSignUpResult():LiveData<String>{
       return signupResult
   }

   fun loginWithEmailPassword(email:String,password:String){
       FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
           .addOnCompleteListener {
               if(!it.isSuccessful) return@addOnCompleteListener
               else signupResult.value = "Login Success"
           }
           .addOnFailureListener {
               signupResult.value = it.message
           }
   }

}

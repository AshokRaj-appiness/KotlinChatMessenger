package com.example.kotlinchatmessenger.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.kotlinchatmessenger.R
import com.example.kotlinchatmessenger.model.User
import com.example.kotlinchatmessenger.viewmodel.SignUpLoginViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class RegisterActivity : AppCompatActivity() {
    val TAG = "RegisterActivity"
    var selectedPhotoUri: Uri ?= null;
    lateinit var signUpLoginViewModel:SignUpLoginViewModel;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        signUpLoginViewModel = ViewModelProviders.of(this).get(SignUpLoginViewModel::class.java)
        listOfObservers()

        btn_register.setOnClickListener {
            val email = email_editext_registration.text.toString()
            val password = password_edittext_registration.text.toString()
            if(email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this,"Please enter Email id/password",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else signUpLoginViewModel.signUpNewUserWithEmailAndPassword(email,password)
        }

        select_photo_button.setOnClickListener {
            Log.d(TAG,"select photo for profile")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkPermissions();
            }


        }

        already_registered_user.setOnClickListener {
            Log.d(TAG,"Move to Login")
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

    }

    fun listOfObservers(){
        signUpLoginViewModel.getSignUpResult().observe(this,object:Observer<String>{
            override fun onChanged(t: String?) {
                Toast.makeText(this@RegisterActivity,t,Toast.LENGTH_SHORT).show()
                uploadToFirebaseStorage()
            }

        })
    }

    private fun uploadToFirebaseStorage() {
        if(selectedPhotoUri == null )return
        val fileName = UUID.randomUUID()
        val ref = FirebaseStorage.getInstance().getReference("/images/$fileName")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d(TAG,"Success upload Image ${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    Log.d(TAG,"File location : $it")
                    saveUserToFireBase(it.toString())
                }
            }
    }

    private fun saveUserToFireBase(downloadUrl:String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/Users/$uid")
        ref.setValue(User(uid,username_editext_registration.text.toString(),downloadUrl))
        val intent = Intent(this,LatestMessagesActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)

    }


    fun checkPermissions(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED||
            ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1052);

        }else{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data !=null){
            Log.d(TAG,"Photo has been picked")
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUri)
            select_photo_button.alpha = 0f
            circular_image_view.setImageBitmap(bitmap)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            when (requestCode) {
                1052 -> {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                        // permission was granted.
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.type = "image/*"
                        startActivityForResult(intent,0)

                    }else {


                        // Permission denied - Show a message to inform the user that this app only works
                        // with these permissions granted

                    }
                    return;
                }
            }

    }

}

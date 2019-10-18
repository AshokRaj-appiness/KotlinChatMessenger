package com.example.kotlinchatmessenger.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.kotlinchatmessenger.R
import com.example.kotlinchatmessenger.viewmodel.SignUpLoginViewModel
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    lateinit var signUpLoginViewModel: SignUpLoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        signUpLoginViewModel = ViewModelProviders.of(this).get(SignUpLoginViewModel::class.java)
        btn_login.setOnClickListener {
           val email = email_editext_registration.text.toString()
           val password = password_editext_registration.text.toString()
           signUpLoginViewModel.loginWithEmailPassword(email,password)
        }
        back_to_registration.setOnClickListener {
            val intent = Intent(this@LoginActivity,RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
        signUpLoginViewModel.getSignUpResult().observe(this,object : Observer<String>{
            override fun onChanged(t: String?) {
                if(t.equals("Login Success")){
                    val intent = Intent(this@LoginActivity,LatestMessagesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }else{
                    Log.d("error",t)
                    Toast.makeText(this@LoginActivity,t,Toast.LENGTH_SHORT).show()
                }

            }

        })
    }

}

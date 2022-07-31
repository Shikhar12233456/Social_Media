package com.example.socialmedia.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.socialmedia.R

class AuthenticationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        supportFragmentManager.beginTransaction()
            .replace(R.id.authfragcontainer, LoginFragment())
            .commit()
    }
}
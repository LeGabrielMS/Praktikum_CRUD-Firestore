package com.example.pertemuan9

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class DefaultActivity : AppCompatActivity() {
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default)
        this.mAuth = FirebaseAuth.getInstance()

        this.loginButton = findViewById(R.id.loginButton)
        this.registerButton = findViewById(R.id.registerButton)

        loginButton.setOnClickListener {
            startActivity(Intent(this@DefaultActivity, LoginActivity::class.java))
        }

        registerButton.setOnClickListener {
            startActivity(Intent(this@DefaultActivity, RegisterActivity::class.java))
        }
        checkUserSession()
    }

    private fun checkUserSession() {
        val currentUser: FirebaseUser? = this.mAuth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this@DefaultActivity, MainActivity::class.java))
            finish()
        }
    }
}
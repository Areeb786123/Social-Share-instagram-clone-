package com.areeb.socialshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_setting.*

class setting : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        logout_btn.setOnClickListener {
            signout()
            val signOutIntent = Intent(this , login::class.java)
            startActivity(signOutIntent)
            finish()

        }
    }

    fun signout() {
        Firebase.auth.signOut()


    }
}
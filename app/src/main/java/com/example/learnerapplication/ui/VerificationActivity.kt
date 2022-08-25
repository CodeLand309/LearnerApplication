package com.example.learnerapplication.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.example.learnerapplication.R
import com.example.learnerapplication.ui.viewModels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VerificationActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)

        val message = intent.extras?.get("Message").toString()
        val client_id = intent.extras?.get("Client ID").toString()

        Toast.makeText(this@VerificationActivity, "$message $client_id", Toast.LENGTH_SHORT).show()

    }
}
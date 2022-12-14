package com.example.learnerapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.learnerapplication.data.preferences.TokenPreferenceManager
import com.example.learnerapplication.ui.activities.HomeActivity
import com.example.learnerapplication.ui.activities.LoginFlowActivity
import com.example.learnerapplication.ui.activities.RegisterActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var tokenManager: TokenPreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        lifecycleScope.launchWhenStarted {
            tokenManager.accessTokenPrefFlow.collect{ tokenPref ->
                tokenPref.apply {
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (details) {
                            startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                            finish()
                        } else {
                            if (otpVerification) {
                                startActivity(Intent(this@MainActivity, RegisterActivity::class.java))
                                finish()
                            } else {
                                startActivity(Intent(this@MainActivity, LoginFlowActivity::class.java))
                                finish()
                            }
                        }
                    }, 1000)
                }
            }
        }
    }
}
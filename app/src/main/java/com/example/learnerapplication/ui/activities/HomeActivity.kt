package com.example.learnerapplication.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.learnerapplication.data.preferences.TokenPreference
import com.example.learnerapplication.data.preferences.TokenPreferenceManager
import com.example.learnerapplication.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private var _binding: ActivityHomeBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var tokenManager: TokenPreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnLogout.setOnClickListener {
                lifecycleScope.launchWhenStarted {
                    val emptyTokenPreference = TokenPreference("", "", false, false)
                    tokenManager.updateAccessTokenPref(emptyTokenPreference)
                    startActivity(Intent(this@HomeActivity, LoginFlowActivity::class.java))
                    finish()
                }
            }
        }
    }
}
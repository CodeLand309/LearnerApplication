package com.example.learnerapplication.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.learnerapplication.R
import com.example.learnerapplication.databinding.ActivityLoginFlowBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFlowActivity : AppCompatActivity() {

    private var _binding : ActivityLoginFlowBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginFlowBinding.inflate(layoutInflater)
        setContentView(binding.root)

       val navHostFragment = supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
        navController = navHostFragment.navController

    }


    override fun onBackPressed() {
        if(navController.currentDestination?.id == R.id.verifyOtpFragment){
            AlertDialog.Builder(this)
                .setTitle("Verification Incomplete!!")
                .setMessage("Do you want to leave Now?")
                .setPositiveButton("Yes") { _, _ -> finish() }
                .setNegativeButton("Change Phone Number") { _, _ -> navController.navigateUp() }
                .show()
        }else{
            super.onBackPressed()
        }
    }
}
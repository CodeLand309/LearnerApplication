package com.example.learnerapplication.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.learnerapplication.R
import com.example.learnerapplication.data.preferences.TokenPreferenceManager
import com.example.learnerapplication.databinding.ActivityRegisterBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var spinnerAdapter: ArrayAdapter<String>
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var school: String
    private lateinit var learnerClass: String
    private lateinit var classes: Array<String>

    @Inject
    lateinit var tokenManager: TokenPreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        classes = resources.getStringArray(R.array.selectClass_Spinner)
        spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, classes)

        binding.apply {

            spinnerSelectClass.adapter = spinnerAdapter

            spinnerSelectClass.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    learnerClass = classes[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    showSnackBar("Select Class", Snackbar.LENGTH_SHORT)
                }

            }

            btnNext.setOnClickListener {
                name = edTextStudentName.text.toString()
                email = edTextStudentEmail.text.toString()
                school = edTextSelectSchool.text.toString()
                if(name.isEmpty() || email.isEmpty() || school.isEmpty() || learnerClass == resources.getStringArray(R.array.selectClass_Spinner)[0]){
                    showSnackBar("Enter All Fields", Snackbar.LENGTH_SHORT)
                }else{
                    //Make Api Call

                    lifecycleScope.launchWhenStarted {
                        tokenManager.updateAccessTokenDetailsKey(true)
                    }
                    Toast.makeText(this@RegisterActivity, "$name\n$email\n$school\n$learnerClass", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegisterActivity, HomeActivity::class.java))
                    finish()
                }
            }
        }

    }

    private fun showSnackBar(msg: String, duration: Int){
        Snackbar.make(binding.root, msg, duration).show()
    }
}
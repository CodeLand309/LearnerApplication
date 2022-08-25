package com.example.learnerapplication.ui

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.example.learnerapplication.LearnerApplication
import com.example.learnerapplication.R
import com.example.learnerapplication.data.model.AuthToken
import com.example.learnerapplication.data.model.Users
import com.example.learnerapplication.databinding.ActivityVerificationBinding
import com.example.learnerapplication.ui.viewModels.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class VerificationActivity : AppCompatActivity() {

    private var _binding: ActivityVerificationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()
    private val TAG: String= "Verification Activity"
    private var isRunning: Boolean = false
    private var timeLeftInMilliSeconds: Long = 60000
    private lateinit var userEnterOTP: String
    private lateinit var countDownTimer: CountDownTimer

    @Inject
    lateinit var learnerApplication: LearnerApplication

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val message = intent.extras?.get("Message").toString()
        val user_id = intent.extras?.get("User ID").toString()
        val mobileNumber = intent.extras?.get("Mobile").toString()

//        Toast.makeText(this@VerificationActivity, "$message $user_id", Toast.LENGTH_SHORT).show()
        val otp = message.trim().replace("""[^0-9]""".toRegex(),"")
//        Thread.sleep(2000)
//        Log.d(TAG, "otp: $otp")
        Toast.makeText(this@VerificationActivity, otp, Toast.LENGTH_SHORT).show()
//        val otpArray = otp.toCharArray()
        binding.apply {

            userPhoneNumber.text = "+91 $mobileNumber"
            pinViewOtp.addTextChangedListener(object: TextWatcher{
                override fun beforeTextChanged(charSequence: CharSequence, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if(charSequence.toString().length==4){
                        userEnterOTP = charSequence.toString()
                    }
                }

                override fun afterTextChanged(editable: Editable?) {

                }

            })

            btnLogin.setOnClickListener {
                if(userEnterOTP.isNotEmpty()) {
                    if (learnerApplication.isNetworkAvailable())
                        viewModel.verifyOtp(userEnterOTP, user_id.toLong())
                    else
                        showSnackBar(getString(R.string.InternetErrorMessage), Snackbar.LENGTH_SHORT)
                }
                else
                    showSnackBar(getString(R.string.OtpNotEnteredMessage), Snackbar.LENGTH_SHORT)
            }

            countDownTimer = object: CountDownTimer(timeLeftInMilliSeconds, 1000){
                override fun onTick(l: Long) {
                    timeLeftInMilliSeconds = l
                    updateTimer()
                }

                override fun onFinish() {
                    cancel()
                    timer.visibility = View.INVISIBLE
                    showSnackBar(getString(R.string.OutTimeOutMessage), Snackbar.LENGTH_SHORT)
                }
            }.start()
        }

        lifecycleScope.launch(Dispatchers.Unconfined) {
            viewModel.eventsFlow.collect { event ->
                when(event){
                    is LoginViewModel.LoginEvents.UserEnterOTP -> {
                        handleOtpVerificationResult(event.details, event.authToken, event.user)
                    }
                    else -> {}
                }
            }
        }
    }

    private fun handleOtpVerificationResult(details: Boolean?, authToken: AuthToken?, user: Users?){
        if(details==null || authToken==null || user==null) {
            showSnackBar(getString(R.string.ServerIssueMessage), Snackbar.LENGTH_SHORT)
        }else{
            countDownTimer.cancel()
            startActivity(Intent(this@VerificationActivity, RegisterActivity::class.java))
        }
    }

    private fun showSnackBar(msg: String, duration: Int){
        Snackbar.make(binding.root, msg, duration).show()
    }

    fun updateTimer(){
        val minutes: Long = (timeLeftInMilliSeconds / 60000)
        val seconds: Long = (timeLeftInMilliSeconds % 60000 / 1000)

        var timeLeftText =""
        if(minutes<10)
            timeLeftText = "0"
        timeLeftText += "$minutes:"
        if(seconds<10)
            timeLeftText += "0"
        timeLeftText += seconds

        binding.timer.text = timeLeftText
    }
}
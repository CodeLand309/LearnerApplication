package com.example.learnerapplication.ui

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
    private val TAG: String = "Verification Activity"
    private var userEnterOTP: String = ""
    private var isRunning: Boolean = true

    @Inject
    lateinit var learnerApplication: LearnerApplication

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val message = intent.extras?.get("Message").toString()
        val user_id = intent.extras?.get("User ID").toString()
        val mobileNumber = intent.extras?.get("Mobile").toString()

        val otpArray = extractOtpFromResponse(message)
        binding.apply {

            userPhoneNumber.text = getString(R.string.countryCodeIndia) + " $mobileNumber"

            if (userEnterOTP.isNotEmpty()) {
                edTextOtp1.setText(otpArray[0].toString())
                edTextOtp2.setText(otpArray[1].toString())
                edTextOtp3.setText(otpArray[2].toString())
                edTextOtp4.setText(otpArray[3].toString())
            }

            btnLogin.setOnClickListener {
                if (learnerApplication.isNetworkAvailable()) {
                    if (btnLogin.text.equals(getString(R.string.GetOTP_button))) {
                        viewModel.cancelTimer()
                        viewModel.getOtp(mobileNumber)
                    } else {
                        readOtp()
                        if (userEnterOTP.isNotEmpty()) {
                            viewModel.verifyOtp(userEnterOTP, user_id.toLong())
                        } else
                            showSnackBar(
                                getString(R.string.OtpNotEnteredMessage),
                                Snackbar.LENGTH_SHORT
                            )
                    }
                } else {
                    showSnackBar(
                        getString(R.string.InternetErrorMessage),
                        Snackbar.LENGTH_SHORT
                    )
                }
            }
            if (!viewModel.timerStartedOnce) {
                timer.visibility = View.VISIBLE
                viewModel.startTimer()
            }

            lifecycleScope.launch(Dispatchers.Unconfined) {
                viewModel.eventsFlow.collect { event ->
                    when (event) {
                        is LoginViewModel.LoginEvents.UserEnterOTP -> {
                            handleOtpVerificationResult(event.details, event.authToken, event.user)
                        }
                        is LoginViewModel.LoginEvents.UserEnterPhoneNumber -> {
                            handleLoginResult(event.message, event.user_id)
                        }
                    }
                }
            }

            viewModel.getTimeLeft().observe(this@VerificationActivity) { timeLeft ->
                timer.text = timeLeft
            }

            viewModel.isRunning.observe(this@VerificationActivity) { running ->
                if (!running) {
                    timer.visibility = View.INVISIBLE
                    if (!viewModel.otpVerified) {
                        edTextOtp1.text.clear()
                        edTextOtp2.text.clear()
                        edTextOtp3.text.clear()
                        edTextOtp4.text.clear()
                        showSnackBar(getString(R.string.OutTimeOutMessage), Snackbar.LENGTH_SHORT)
                        btnLogin.text = getString(R.string.GetOTP_button)
                    }
                } else {
                    isRunning = running
                    timer.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun extractOtpFromResponse(message: String): CharArray {
        userEnterOTP = message.trim().replace("""[^0-9]""".toRegex(), "")
        Toast.makeText(this@VerificationActivity, userEnterOTP, Toast.LENGTH_SHORT).show()
        return userEnterOTP.toCharArray()
    }

    private fun readOtp() {
        binding.apply {
            val otp1 = edTextOtp1.text.toString()
            val otp2 = edTextOtp2.text.toString()
            val otp3 = edTextOtp3.text.toString()
            val otp4 = edTextOtp4.text.toString()
            if (otp1.isNotEmpty() || otp2.isNotEmpty() || otp3.isNotEmpty() || otp4.isNotEmpty())
                userEnterOTP = otp1 + otp2 + otp3 + otp4
        }
    }

    private fun handleLoginResult(message: String?, userId: Long?) {
        if (userId == null || message == null) {
            showSnackBar(getString(R.string.ServerIssueMessage), Snackbar.LENGTH_SHORT)
        } else {
            extractOtpFromResponse(message)
            viewModel.startTimer()
            binding.btnLogin.text = getString(R.string.login_Button)
        }
    }

    private fun handleOtpVerificationResult(
        details: Boolean?,
        authToken: AuthToken?,
        user: Users?
    ) {
        if (details == null || authToken == null || user == null) {
            showSnackBar(getString(R.string.ServerIssueMessage), Snackbar.LENGTH_SHORT)
        } else {
            binding.timer.visibility = View.VISIBLE
            viewModel.cancelTimer()
            if (details) {
                startActivity(Intent(this@VerificationActivity, HomeActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this@VerificationActivity, RegisterActivity::class.java))
                finish()
            }
        }
    }

    private fun showSnackBar(msg: String, duration: Int) {
        Snackbar.make(binding.root, msg, duration).show()
    }
}
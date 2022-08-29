package com.example.learnerapplication.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.learnerapplication.LearnerApplication
import com.example.learnerapplication.R
import com.example.learnerapplication.data.model.AuthToken
import com.example.learnerapplication.data.model.Users
import com.example.learnerapplication.databinding.FragmentVerifyOtpBinding
import com.example.learnerapplication.ui.activities.HomeActivity
import com.example.learnerapplication.ui.activities.RegisterActivity
import com.example.learnerapplication.ui.activities.viewModels.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class VerifyOtpFragment : Fragment(R.layout.fragment_verify_otp) {

    private var _binding: FragmentVerifyOtpBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()
    private val TAG: String = "Verification Activity"
    private var userEnterOTP: String = ""
    private var isRunning: Boolean = true
    private val args: VerifyOtpFragmentArgs by navArgs()

    @Inject
    lateinit var learnerApplication: LearnerApplication

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentVerifyOtpBinding.bind(view)

        val message = args.message
        val user_id = args.userID
        val mobileNumber = args.mobile

        val otpArray = extractOtpFromResponse(message)
        binding.apply {

            userPhoneNumber.text = getString(R.string.countryCodeIndia) + " $mobileNumber"

            if(viewModel.timeOutMsgShown)
                btnLogin.text = getString(R.string.GetOTP_button)

            if (userEnterOTP.isNotEmpty()) {
                edTextOtp1.setText(otpArray[0].toString())
                edTextOtp2.setText(otpArray[1].toString())
                edTextOtp3.setText(otpArray[2].toString())
                edTextOtp4.setText(otpArray[3].toString())
            }

            btnLogin.setOnClickListener {
                if (learnerApplication.isNetworkAvailable()) {
                    if (checkBtnText()) {
                        viewModel.cancelTimer()
                        viewModel.getOtp(mobileNumber)
                    } else {
                        readOtp()
                        if (userEnterOTP.isNotEmpty()) {
                            viewModel.verifyOtp(userEnterOTP, user_id)
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

            viewModel.getTimeLeft().observe(viewLifecycleOwner) { timeLeft ->
                timer.text = timeLeft
            }

            viewModel.isRunning.observe(viewLifecycleOwner) { running ->
                if (!running) {
                    timer.visibility = View.INVISIBLE
                    if (!viewModel.otpVerified && !viewModel.timeOutMsgShown) {
                        edTextOtp1.text.clear()
                        edTextOtp2.text.clear()
                        edTextOtp3.text.clear()
                        edTextOtp4.text.clear()
                        showSnackBar(getString(R.string.TimeOutMessage), Snackbar.LENGTH_SHORT)
                        btnLogin.text = getString(R.string.GetOTP_button)
                        viewModel.timeOutMsgShown = true
                    }
                } else {
                    isRunning = running
                    timer.visibility = View.VISIBLE
                }
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkBtnText() = binding.btnLogin.text.equals(getString(R.string.GetOTP_button))

    private fun extractOtpFromResponse(message: String): CharArray {
        userEnterOTP = message.trim().replace("""[^0-9]""".toRegex(), "")
        if(!viewModel.timerStartedOnce || checkBtnText())
            Toast.makeText(context, userEnterOTP, Toast.LENGTH_SHORT).show()
        return userEnterOTP.toCharArray()
    }

    private fun readOtp() {
        binding.apply {
            userEnterOTP=""
            val otp1 = edTextOtp1.text.toString()
            val otp2 = edTextOtp2.text.toString()
            val otp3 = edTextOtp3.text.toString()
            val otp4 = edTextOtp4.text.toString()
            if (otp1.isNotEmpty() && otp2.isNotEmpty() && otp3.isNotEmpty() && otp4.isNotEmpty())
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
                startActivity(Intent(requireActivity(), HomeActivity::class.java))
                requireActivity().finish()
            } else {
                startActivity(Intent(requireActivity(), RegisterActivity::class.java))
                requireActivity().finish()
            }
        }
    }

    private fun showSnackBar(msg: String, duration: Int) {
        Snackbar.make(binding.root, msg, duration).show()
    }
}
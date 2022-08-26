package com.example.learnerapplication.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.learnerapplication.LearnerApplication
import com.example.learnerapplication.R
import com.example.learnerapplication.data.model.SliderData
import com.example.learnerapplication.databinding.ActivityLoginBinding
import com.example.learnerapplication.ui.adapters.SliderAdapter
import com.example.learnerapplication.ui.viewModels.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import com.smarteist.autoimageslider.SliderView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var phoneNumber: String
    private val viewModel : LoginViewModel by viewModels()

    @Inject
    lateinit var learnerApplication: LearnerApplication

    private val image1 = R.drawable.image1
    private val image2 = R.drawable.image2
    private val image3 = R.drawable.image3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sliderDataList = ArrayList<SliderData>()
        lateinit var sliderView: SliderView

        binding.apply {
            sliderView = slider
            sliderDataList.add(SliderData(image1))
            sliderDataList.add(SliderData(image2))
            sliderDataList.add(SliderData(image3))
            val adapter = SliderAdapter(sliderDataList)
            sliderView.autoCycleDirection = SliderView.LAYOUT_DIRECTION_LTR
            sliderView.setSliderAdapter(adapter)
            sliderView.scrollTimeInSec = 3
            sliderView.isAutoCycle = true
            sliderView.startAutoCycle()

            btnGetOtp.setOnClickListener {
                if(!learnerApplication.isNetworkAvailable()){
                    showSnackBar(getString(R.string.InternetErrorMessage), Snackbar.LENGTH_SHORT)
                }else {
//                    startActivity(Intent(this@LoginActivity, VerificationActivity::class.java))
                    phoneNumber = edTextPhone.text.toString()
                    if(phoneNumber.length!=10)
                        showSnackBar(getString(R.string.PhoneNumberIncorrectMessage), Snackbar.LENGTH_SHORT)
                    else
                        viewModel.getOtp(phoneNumber)
                }
            }
        }

        lifecycleScope.launch(Dispatchers.Unconfined) {
            viewModel.eventsFlow.collect{ event ->
                when(event){
                    is LoginViewModel.LoginEvents.UserEnterPhoneNumber -> {
                        handleLoginResult(event.message, event.user_id, event.phoneNumber)
                    }
                    else -> {}
                }
            }
        }

    }
    private fun showSnackBar(msg: String, duration: Int){
        Snackbar.make(binding.root, msg, duration).show()
    }

    private fun handleLoginResult(message: String?, user_id: Long?, phoneNumber: String?) {
        if(user_id==null){
            showSnackBar(getString(R.string.ServerIssueMessage), Snackbar.LENGTH_SHORT)
        }else {
            Intent(this@LoginActivity, VerificationActivity::class.java).apply {
                putExtra("Message", message)
                putExtra("User ID", user_id)
                putExtra("Mobile", phoneNumber)
                startActivity(this)
            }
        }
    }

}
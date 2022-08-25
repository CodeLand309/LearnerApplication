package com.example.learnerapplication.ui

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.learnerapplication.R
import com.example.learnerapplication.data.model.SliderData
import com.example.learnerapplication.databinding.ActivityLoginBinding
import com.example.learnerapplication.ui.adapters.SliderAdapter
import com.example.learnerapplication.ui.viewModels.LoginViewModel
import com.smarteist.autoimageslider.SliderView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var phoneNumber: String
    private val viewModel : LoginViewModel by viewModels()
    val image1 = R.drawable.image1

    val image2 = R.drawable.image2
//        "https://qphs.fs.quoracdn.net/main-qimg-8e203d34a6a56345f86f1a92570557ba.webp";
    val image3 = R.drawable.image3
//        "https://bizzbucket.co/wp-content/uploads/2020/08/Life-in-The-Metro-Blog-Title-22.png";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sliderDataList = ArrayList<SliderData>()

        lateinit var sliderView: SliderView

        supportActionBar?.hide()

        binding.apply {
            sliderView = slider
            sliderDataList.add(SliderData(image1))
            sliderDataList.add(SliderData(image2))
            sliderDataList.add(SliderData(image3))
            val adapter: SliderAdapter = SliderAdapter(this@LoginActivity, sliderDataList)
            sliderView.autoCycleDirection = SliderView.LAYOUT_DIRECTION_LTR
            sliderView.setSliderAdapter(adapter)
            sliderView.scrollTimeInSec = 3
            sliderView.isAutoCycle = true;
            sliderView.startAutoCycle();

            btnGetOtp.setOnClickListener {
                phoneNumber = editTextPhone.text.toString()
                viewModel.login(phoneNumber)
            }
        }

        lifecycleScope.launch(Dispatchers.Unconfined) {
            viewModel.eventsFlow.collect(){ event ->
                when(event){
                    is LoginViewModel.LoginPageEvent.UserEnterPhoneNumber -> {
                        handleLoginResult(event.message, event.user_id)
                    }
                }
            }
        }

    }

    private fun handleLoginResult(message: String?, user_id: Long?) {
        Intent(this@LoginActivity, VerificationActivity::class.java).apply {
            putExtra("Message", message)
            putExtra("Client ID", user_id)
            startActivity(this)
        }
    }

}
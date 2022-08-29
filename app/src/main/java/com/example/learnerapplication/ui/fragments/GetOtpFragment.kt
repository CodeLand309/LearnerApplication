package com.example.learnerapplication.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.learnerapplication.LearnerApplication
import com.example.learnerapplication.R
import com.example.learnerapplication.data.model.SliderData
import com.example.learnerapplication.databinding.FragmentGetOtpBinding
import com.example.learnerapplication.ui.activities.viewModels.LoginViewModel
import com.example.learnerapplication.ui.adapters.SliderAdapter
import com.google.android.material.snackbar.Snackbar
import com.smarteist.autoimageslider.SliderView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class GetOtpFragment : Fragment(R.layout.fragment_get_otp){

    private var _binding: FragmentGetOtpBinding? = null
    private val binding get() = _binding!!
    private lateinit var phoneNumber: String
    private val viewModel : LoginViewModel by viewModels()

    @Inject
    lateinit var learnerApplication: LearnerApplication

    private val image1 = R.drawable.image1
    private val image2 = R.drawable.image2
    private val image3 = R.drawable.image3


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentGetOtpBinding.bind(view)


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
                    phoneNumber = edTextPhone.text.toString()
                    if(phoneNumber.length!=10)
                        showSnackBar(getString(R.string.PhoneNumberIncorrectMessage), Snackbar.LENGTH_SHORT)
                    else{
//                        val action = GetOtpFragmentDirections.actionGetOtpFragmentToVerifyOtpFragment()
//                            .setMessage("This is your OTP: 1234")
//                            .setUserID(8)
//                            .setMobile(phoneNumber)
//                        requireActivity().findNavController(R.id.nav_host_fragment).navigate(action)
                        viewModel.getOtp(phoneNumber)
                    }
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
            val action = GetOtpFragmentDirections.actionGetOtpFragmentToVerifyOtpFragment()
                .setMessage(message!!)
                .setUserID(user_id)
                .setMobile(phoneNumber!!)
                requireActivity().findNavController(R.id.nav_host_fragment).navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

}
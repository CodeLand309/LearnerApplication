package com.example.learnerapplication.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learnerapplication.data.model.AuthResponse
import com.example.learnerapplication.data.model.AuthToken
import com.example.learnerapplication.data.model.Users
import com.example.learnerapplication.data.model.VerificationResponse
import com.example.learnerapplication.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository,
): ViewModel() {

    private val eventsChannel = Channel<LoginEvents>()
    val eventsFlow = eventsChannel.receiveAsFlow()
    var resultLogin: AuthResponse? = null
    var resultVerifyOtp: VerificationResponse? = null

    fun login(phoneNumber: String) {
        viewModelScope.launch{
            resultLogin = repository.loginAsync(phoneNumber).await()
            if(resultLogin != null){
                eventsChannel.send(LoginEvents.UserEnterPhoneNumber(resultLogin!!.message, resultLogin!!.user_id, phoneNumber))
            } else{
                eventsChannel.send(LoginEvents.UserEnterPhoneNumber(null, null, null))
            }
        }
    }

    fun verifyOtp(otp: String, user_id: Long){
        viewModelScope.launch {
            resultVerifyOtp = repository.verifyOtpAsync(otp, user_id).await()
            if(resultVerifyOtp != null){
                eventsChannel.send(LoginEvents.UserEnterOTP(resultVerifyOtp!!.user, resultVerifyOtp!!.details, resultVerifyOtp!!.authToken))
            }else{
                eventsChannel.send(LoginEvents.UserEnterOTP(null, null, null))
            }
        }
    }

    sealed class LoginEvents{
        data class UserEnterPhoneNumber(val message: String?, val user_id: Long?, val phoneNumber: String?): LoginEvents()
        data class UserEnterOTP(val user: Users?, val details: Boolean?, val authToken: AuthToken?): LoginEvents()
    }

}
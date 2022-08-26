package com.example.learnerapplication.ui.viewModels

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learnerapplication.data.model.AuthResponse
import com.example.learnerapplication.data.model.AuthToken
import com.example.learnerapplication.data.model.Users
import com.example.learnerapplication.data.model.VerificationResponse
import com.example.learnerapplication.data.preferences.TokenPreference
import com.example.learnerapplication.data.preferences.TokenPreferenceManager
import com.example.learnerapplication.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val preferenceManager: TokenPreferenceManager,
): ViewModel() {

    private val eventsChannel = Channel<LoginEvents>()
    val eventsFlow = eventsChannel.receiveAsFlow()
    private var resultLogin: AuthResponse? = null
    private var resultVerifyOtp: VerificationResponse? = null
    private var timeLeftInMilliSeconds: Long = 60000
    private lateinit var countDownTimer: CountDownTimer
    var isRunning: MutableLiveData<Boolean> = MutableLiveData(true)
    private var timeLeftString: MutableLiveData<String> = MutableLiveData()
    var timerStartedOnce: Boolean = false
    var otpVerified: Boolean = false

    fun getOtp(phoneNumber: String) {
        viewModelScope.launch{
            resultLogin = repository.loginAsync(phoneNumber).await()
            if(resultLogin != null){
                eventsChannel.send(LoginEvents.UserEnterPhoneNumber(resultLogin!!.message, resultLogin!!.user_id, phoneNumber))
            } else{
                eventsChannel.send(LoginEvents.UserEnterPhoneNumber(null, null, null))
            }
        }
    }

    fun getTimeLeft(): LiveData<String>{
        return timeLeftString
    }

    fun verifyOtp(otp: String, user_id: Long){
        viewModelScope.launch {
            resultVerifyOtp = repository.verifyOtpAsync(otp, user_id).await()
            if(resultVerifyOtp != null){
                resultVerifyOtp!!.run {
                    val authToken = authToken
                    preferenceManager.updateAccessTokenPref(TokenPreference(authToken.accessToken, authToken.refreshToken, true))
                    otpVerified=true
                    eventsChannel.send(LoginEvents.UserEnterOTP(user, details, authToken))
                }
            }else{
                otpVerified=false
                eventsChannel.send(LoginEvents.UserEnterOTP(null, null, null))
            }
        }
    }

    fun startTimer(){
        timeLeftInMilliSeconds=60000
        timerStartedOnce = true
        countDownTimer = object: CountDownTimer(timeLeftInMilliSeconds, 1000){
            override fun onTick(l: Long) {
                if(isRunning.value==false)
                    isRunning.value = true
                timeLeftInMilliSeconds = l
                updateTimer()
            }

            override fun onFinish() {
                isRunning.value = false
                cancel()
            }
        }.start()
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
        timeLeftString.value = timeLeftText
    }

    fun cancelTimer() {
        if (isRunning.value==true) {
            isRunning.value = false
            countDownTimer.cancel()
        }
    }

    sealed class LoginEvents{
        data class UserEnterPhoneNumber(val message: String?, val user_id: Long?, val phoneNumber: String?): LoginEvents()
        data class UserEnterOTP(val user: Users?, val details: Boolean?, val authToken: AuthToken?): LoginEvents()
    }

}
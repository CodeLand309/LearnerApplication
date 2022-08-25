package com.example.learnerapplication.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learnerapplication.data.model.AuthResponse
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

    private val eventsChannel = Channel<LoginPageEvent>()
    val eventsFlow = eventsChannel.receiveAsFlow()
    var result: AuthResponse? = null

    fun login(phoneNumber: String) {
        viewModelScope.launch{
            result = repository.loginAsync(phoneNumber).await()
            if(result != null){
                eventsChannel.send(LoginPageEvent.UserEnterPhoneNumber(result!!.message, result!!.user_id))
            } else{
                eventsChannel.send(LoginPageEvent.UserEnterPhoneNumber("There was some server issue", null))
            }
        }
    }

    sealed class LoginPageEvent{
        data class UserEnterPhoneNumber(val message: String?, val user_id: Long?): LoginPageEvent()
    }

}
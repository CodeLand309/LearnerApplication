package com.example.learnerapplication.ui.activities.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learnerapplication.data.model.Users
import com.example.learnerapplication.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository
): ViewModel() {

    private val eventsChannel = Channel<LoginViewModel.LoginEvents>()
    val eventsFlow = eventsChannel.receiveAsFlow()
    var resultVerification: Users? = null

    fun newUserRegistration(user: Users){
        viewModelScope.launch {
            resultVerification = repository.newUserRegistrationAsync(user).await()
            if(resultVerification != null){
                //TODO
            } else{
                //TODO
            }
        }
    }

    sealed class RegisterEvent{
        data class UserEnterPhoneNumber(val user: Users?): RegisterEvent()
    }

}
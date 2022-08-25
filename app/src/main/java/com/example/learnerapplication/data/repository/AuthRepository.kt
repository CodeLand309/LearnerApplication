package com.example.learnerapplication.data.repository

import android.util.Log
import com.example.learnerapplication.data.model.AuthResponse
import com.example.learnerapplication.data.model.Users
import com.example.learnerapplication.data.model.VerificationResponse
import com.example.learnerapplication.data.network.ApiInterface
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

const val TAG = "AuthRepository"

@Singleton
class AuthRepository @Inject constructor(
    private val apiInterface: ApiInterface
) {

    fun loginAsync(phoneNumber: String): Deferred<AuthResponse?>{
        val result = CoroutineScope(Dispatchers.IO).async {
            val response = apiInterface.login(phoneNumber)
            if(response.isSuccessful){
                response.body()
            }else{
                null
            }
        }
        return result
    }

    fun verifyOtpAsync(otp: String, user_id: Long): Deferred<VerificationResponse?>{
        val result = CoroutineScope(Dispatchers.IO).async {
            val response = apiInterface.verifyOTP(otp, user_id)
            if(response.isSuccessful){
                response.body()
            }else{
                null
            }
        }
        return result
    }

    fun newUserRegistrationAsync(user: Users): Deferred<Users?>{
        val result = CoroutineScope(Dispatchers.IO).async {
            val response = apiInterface.newUserRegistration(user)
            if(response.isSuccessful){
                response.body()
            }else{
                null
            }
        }
        return result
    }

}
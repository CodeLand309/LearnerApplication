package com.example.learnerapplication.data.repository

import com.example.learnerapplication.data.model.AuthResponse
import com.example.learnerapplication.data.network.ApiInterface
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

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

}
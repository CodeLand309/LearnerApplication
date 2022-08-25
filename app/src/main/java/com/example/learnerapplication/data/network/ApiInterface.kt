package com.example.learnerapplication.data.network

import com.example.learnerapplication.data.model.AuthResponse
import com.example.learnerapplication.data.model.Users
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

//    @Headers(@Header("X-App-Type") x_app_type: String = "learner")
    @Headers("Accept: application/json", "X-App-Type: learner")
    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("mobile") mobile: String, @Field("client_id") client_id: Long = 8,
        @Field("device_id") device_id: String = "3d0cd218875efb07",
        @Field("device_type") device_type: String = "android",
        @Field("firebase_token") firebase_token: String = "vvvvvvv"
    ): Response<AuthResponse>

    @FormUrlEncoded
    @POST("verify-otp/{client_id}")
    suspend fun verifyOTP(
        @Field("otp") otp: Long,
        @Field("client_id") client_id: Long=8
    )

    @POST("users")
    suspend fun updateProfile(@Body user: Users)

}
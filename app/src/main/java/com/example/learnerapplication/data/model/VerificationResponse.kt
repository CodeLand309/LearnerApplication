package com.example.learnerapplication.data.model

import com.google.gson.annotations.SerializedName

data class VerificationResponse(
    @SerializedName("details") val details: Boolean,
    @SerializedName("token") val authToken: AuthToken,
    @SerializedName("user") val user: Users
)

package com.example.learnerapplication.data.model

import com.google.gson.annotations.SerializedName

data class Users(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("default_segment_id") val default_segment_id: Long,
    @SerializedName("collage") val college: String,
    @SerializedName("profile_picture") val profilePicUrl: String,
    @SerializedName("parent_name") val parent_name: String,
    @SerializedName("address") val address: String,
    @SerializedName("dob") val dob: String
)

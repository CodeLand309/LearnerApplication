package com.example.learnerapplication.data.model

import com.google.gson.annotations.SerializedName
import java.time.format.DateTimeFormatter

data class Users(
    @SerializedName("name") val name: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("default_segment_id") val defaultSegmentId: Long?,
    @SerializedName("collage") val college: String?,
    @SerializedName("profile_picture") val profilePicUrl: String?,
    @SerializedName("parent_name") val parentName: String?,
    @SerializedName("parent_email") val parentEmail: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("dob") val dob: String?,

    @SerializedName("id") val id: Long?,
    @SerializedName("mobile") val mobile: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("client_id") val clientId: Long?,
    @SerializedName("unique_id") val uniqueId: String?,
    @SerializedName("type") val type: String?

)

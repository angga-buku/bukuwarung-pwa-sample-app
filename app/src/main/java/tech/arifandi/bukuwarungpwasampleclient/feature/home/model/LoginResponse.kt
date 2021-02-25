package tech.arifandi.bukuwarungpwasampleclient.feature.home.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

data class LoginResponse(
    @Json(name = "success")
    val success: Boolean? = false,
    @Json(name = "userId")
    val userId: String,
    @Json(name = "guestId")
    val guestId: String? = null,
    @Json(name = "deviceId")
    val deviceId: String? = null,
    @Json(name = "sessionToken")
    val sessionToken: String,
    @Json(name = "idToken")
    val idToken: String,
    @Json(name = "countryCode")
    val countryCode: String,
    @Json(name = "phone")
    val phone: String,
    @Json(name = "message")
    val message: String? = null,
    @Json(name = "newUser")
    val newUser: Boolean? = false,
)
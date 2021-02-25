package tech.arifandi.bukuwarungpwasampleclient.feature.home.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

data class LoginRequest(
        @Json(name = "countryCode")
    val countryCode: String,
        @Json(name = "phone")
    val phone: String,
        @Json(name = "otp")
    val otp: String,
        @Json(name = "deviceId")
    val deviceId: String? = null,
        @Json(name = "deviceModel")
    val deviceModel: String? = null,
        @Json(name = "deviceBrand")
    val deviceBrand: String? = null,
        @Json(name = "androidId")
    val androidId: String? = null,
        @Json(name = "guestId")
    val guestId: String? = null,
        @Json(name = "client")
    val client: String,
        @Json(name = "clientSecret")
    val clientSecret: String,
)
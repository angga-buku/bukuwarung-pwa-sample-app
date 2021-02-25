package tech.arifandi.bukuwarungpwasampleclient.feature.home.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

data class SendOtpRequest(
    @Json(name = "countryCode")
    val countryCode: String,
    @Json(name = "phone")
    val phone: String,
    @Json(name = "method")
    val method: String,
    @Json(name = "action")
    val action: String,
    @Json(name = "deviceId")
    val deviceId: String? = null,
    @Json(name = "clientId")
    val clientId: String,
    @Json(name = "clientSecret")
    val clientSecret: String,
)
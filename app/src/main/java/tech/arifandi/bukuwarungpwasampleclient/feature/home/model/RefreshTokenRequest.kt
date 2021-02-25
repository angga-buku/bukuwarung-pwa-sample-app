package tech.arifandi.bukuwarungpwasampleclient.feature.home.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

data class RefreshTokenRequest(
    @Json(name = "token")
    val token: String,
    @Json(name = "deviceId")
    val deviceId: String? = null,
    @Json(name = "register")
    val register: Boolean? = null,
    @Json(name = "deviceModel")
    val deviceModel: String? = null,
    @Json(name = "deviceBrand")
    val deviceBrand: String? = null,
    @Json(name = "userId")
    val userId: String,
    @Json(name = "clientId")
    val clientId: String,
    @Json(name = "clientSecret")
    val clientSecret: String,
)
package tech.arifandi.bukuwarungpwasampleclient.feature.home.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

data class RefreshTokenResponse(
    @Json(name = "sessionToken")
    val sessionToken: String,
    @Json(name = "idToken")
    val idToken: String,
)
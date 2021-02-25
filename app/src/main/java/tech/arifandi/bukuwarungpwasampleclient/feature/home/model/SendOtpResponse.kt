package tech.arifandi.bukuwarungpwasampleclient.feature.home.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SendOtpResponse(
    val status: String,
    val token: String,
    val message: String,
    val recipient: String
): Parcelable
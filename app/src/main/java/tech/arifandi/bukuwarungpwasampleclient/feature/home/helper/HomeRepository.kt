package tech.arifandi.bukuwarungpwasampleclient.feature.home.helper

import com.squareup.moshi.Moshi
import tech.arifandi.bukuwarungpwasampleclient.shared.repositories.BaseRepository
import kotlinx.coroutines.Deferred
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import tech.arifandi.bukuwarungpwasampleclient.feature.home.model.*
import tech.arifandi.bukuwarungpwasampleclient.shared.k.KCredential
import java.lang.Exception


interface HomeApi {
    @POST("auth/otp/send")
    fun sendOtp(@Body body: SendOtpRequest): Deferred<SendOtpResponse>

    @POST("auth/login/standalone")
    fun login(@Body body: LoginRequest, @Header("x-ops-token") opsToken: String): Deferred<LoginResponse>

    @POST("auth/users/bacon")
    fun refreshToken(@Body body: RefreshTokenRequest): Deferred<RefreshTokenResponse>

}

class ApiException: Exception()

@Suppress("BlockingMethodInNonBlockingContext")
class HomeRepository(private val api: HomeApi) : BaseRepository() {

    suspend fun sendOtp(
        countryCode: String,
        phone: String,
        method: String,
    ): SendOtpResponse? {
        val json = SendOtpRequest(
            countryCode = countryCode,
            phone = phone,
            method = method,
            action = "SEND",
            clientId = KCredential.ClientId,
            clientSecret = KCredential.ClientSecret,
        )

        return api.sendOtp(json).await()
    }

    suspend fun login(
        countryCode: String,
        phone: String,
        otp: String,
        opsToken: String,
    ): LoginResponse? {
        val json = LoginRequest(
            countryCode = countryCode,
            phone = phone,
            otp = otp,
            client = KCredential.ClientId,
            clientSecret = KCredential.ClientSecret,
        )

        return api.login(json, opsToken).await()
    }

    suspend fun refreshToken(
        refreshToken: String,
        userId: String,
    ): RefreshTokenResponse? {
        val json = RefreshTokenRequest(
            userId = userId,
            token = refreshToken,
            clientId = KCredential.ClientId,
            clientSecret = KCredential.ClientSecret,
        )

        return api.refreshToken(json).await()
    }

}
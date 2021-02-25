package tech.arifandi.bukuwarungpwasampleclient.feature.home.vm

import android.content.SharedPreferences
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tech.arifandi.bukuwarungpwasampleclient.feature.home.helper.HomeRepository
import tech.arifandi.bukuwarungpwasampleclient.shared.repositories.ApiFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tech.arifandi.bukuwarungpwasampleclient.shared.helper.PreferenceHelper.get
import tech.arifandi.bukuwarungpwasampleclient.shared.helper.PreferenceHelper.set
import tech.arifandi.bukuwarungpwasampleclient.shared.k.KCredential
import tech.arifandi.bukuwarungpwasampleclient.shared.k.KEnum
import java.util.*

enum class HomeViewModelState {
    IDLE,
    LOADING,
    ERROR
}

enum class HomeViewModelStepState {
    SEND_OTP,
    VERIFY_OTP,
    CHECKING_TOKEN,
    JUMP_TO_PWA,
    IDLE
}

data class HomeState(
    var state: HomeViewModelState = HomeViewModelState.IDLE,
    var stepState: HomeViewModelStepState = HomeViewModelStepState.IDLE,
    var loading: Boolean = false,
    var btnEnabled: Boolean = false,
    var error: Any? = null,
    var session: String? = null,
)

class HomeViewModel(
    private val sharedPreferences: SharedPreferences,
) : ViewModel() {

    private val repository = HomeRepository(ApiFactory.homeApi)

    private var token: String? = null
    private var refreshToken: String? = null
    private var opsToken: String? = null
    var state = MediatorLiveData<HomeState>()
    var phone = MutableLiveData<String>()
    var otp = MutableLiveData<String>()

    var countryCode: String = "+62"
    var method: String = "SMS" // or can be WA

    init {
        state = MediatorLiveData()
        state.addSource(
            phone
        ) {
            val currentState = state.value ?: HomeState()
            val stepStateValue = state.value?.stepState ?: HomeViewModelStepState.IDLE
            if (stepStateValue == HomeViewModelStepState.SEND_OTP) {
                state.value = currentState.copy(
                    btnEnabled = !phone.value.isNullOrEmpty()
                )
            }
        }
        state.addSource(
            otp
        ) {
            val currentState = state.value ?: HomeState()
            val stepStateValue = state.value?.stepState ?: HomeViewModelStepState.IDLE
            if (stepStateValue == HomeViewModelStepState.VERIFY_OTP) {
                state.value = currentState.copy(
                    btnEnabled = !otp.value.isNullOrEmpty()
                )
            }
        }
    }

    fun onPhoneChanged(text: String) {
        phone.value = text
    }

    fun onOTPChanged(text: String) {
        otp.value = text
    }

    private fun loading(bool: Boolean, stepState: HomeViewModelStepState? = null) {
        var currentState = state.value ?: HomeState()
        var load = HomeViewModelState.IDLE
        if (bool) {
            load = HomeViewModelState.LOADING
        }
        state.value = currentState.copy(
            loading = bool,
            state = load,
            error = null,
        )
        currentState = state.value ?: HomeState()
        if (stepState != null) {
            state.value = currentState.copy(
                state = load,
                loading = bool,
                error = null,
                stepState = stepState,
            )
        }
    }

    private fun error(error: java.lang.Exception) {
        val currentState = state.value ?: HomeState()
        state.value = currentState.copy(
            loading = false,
            error = error,
            state = HomeViewModelState.ERROR,
        )
    }

    fun initialize() {
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                loading(true, stepState = HomeViewModelStepState.CHECKING_TOKEN)
                val token: String? = sharedPreferences[KEnum.Companion.SharedPref.Token.name, null]
                val refreshToken: String? =
                    sharedPreferences[KEnum.Companion.SharedPref.RefreshToken.name, null]
                val loginAt: Long? =
                    sharedPreferences[KEnum.Companion.SharedPref.LoginAt.name, null]
                val userId: String? =
                    sharedPreferences[KEnum.Companion.SharedPref.UserId.name, null]

                if (token.isNullOrEmpty() && refreshToken.isNullOrEmpty()) {
                    // never logged in before
                    val currentState = state.value ?: HomeState()
                    state.value = currentState.copy(
                        stepState = HomeViewModelStepState.SEND_OTP,
                        state = HomeViewModelState.IDLE,
                    )
                } else {
                    // check if login expires
                    val loginTime = Date(loginAt ?: 0L)
                    val expiredTime = loginTime.time + KCredential.TokenExpirationInMillis
                    val now = System.currentTimeMillis()

                    if (now > expiredTime) {
                        try {
                            // token expired, refresh it
                            refreshToken()
                            goToPWA()
                        } catch (ex: java.lang.Exception) {
                            error(ex)
                        }
                    } else {
                        // token not expired, directly jump to PWA
                        goToPWA()
                    }
                }
            }
        }
    }

    fun sendOtp() {
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                loading(true)
                try {
                    val resp = repository.sendOtp(
                        countryCode = countryCode,
                        phone = phone.value!!,
                        method = method,
                    )
                    opsToken = resp?.token
                    loading(false, stepState = HomeViewModelStepState.VERIFY_OTP)
                } catch (ex: Exception) {
                    error(ex)
                }
            }
        }
    }

    fun login() {
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                loading(true)
                try {
                    val resp = repository.login(
                        countryCode = countryCode,
                        phone = phone.value!!,
                        otp = otp.value!!,
                        opsToken = opsToken!!
                    )
                    refreshToken = resp?.sessionToken
                    token = resp?.idToken
                    goToPWA()
                } catch (ex: java.lang.Exception) {
                    error(ex)
                }
            }
        }
    }

    private fun goToPWA() {
        if (!phone.value.isNullOrEmpty()) {
            sharedPreferences[KEnum.Companion.SharedPref.UserId.name] = phone.value
        }
        if (!token.isNullOrEmpty()) {
            sharedPreferences[KEnum.Companion.SharedPref.Token.name] = token
            sharedPreferences[KEnum.Companion.SharedPref.LoginAt.name] = System.currentTimeMillis()
        }
        if (!refreshToken.isNullOrEmpty()) {
            sharedPreferences[KEnum.Companion.SharedPref.RefreshToken.name] =
                refreshToken
        }

        loading(false, stepState = HomeViewModelStepState.JUMP_TO_PWA)
    }

    private fun refreshToken() {
        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                loading(true)
                val resp = repository.refreshToken(
                    refreshToken = sharedPreferences[KEnum.Companion.SharedPref.RefreshToken.name, ""]!!,
                    userId = sharedPreferences[KEnum.Companion.SharedPref.UserId.name, ""]!!,
                )
                refreshToken = resp?.sessionToken
                token = resp?.idToken
                loading(false)
            }
        }
    }
}
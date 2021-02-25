package tech.arifandi.bukuwarungpwasampleclient.feature.home.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import tech.arifandi.bukuwarungpwasampleclient.R
import tech.arifandi.bukuwarungpwasampleclient.feature.home.vm.HomeViewModel
import tech.arifandi.bukuwarungpwasampleclient.shared.extension.getViewModel
import kotlinx.android.synthetic.main.activity_home.*
import tech.arifandi.bukuwarungpwasampleclient.feature.home.vm.HomeViewModelState
import tech.arifandi.bukuwarungpwasampleclient.feature.home.vm.HomeViewModelStepState
import tech.arifandi.bukuwarungpwasampleclient.feature.pwa.PwaActivity
import tech.arifandi.bukuwarungpwasampleclient.shared.helper.PreferenceHelper
import tech.arifandi.bukuwarungpwasampleclient.shared.helper.PreferenceHelper.get
import tech.arifandi.bukuwarungpwasampleclient.shared.k.KEnum

class HomeActivity : AppCompatActivity() {

    private val vm: HomeViewModel by lazy {
        getViewModel { HomeViewModel(
            sharedPreferences = PreferenceHelper.defaultPrefs(this)
        ) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setUI()
        setVM()
    }

    private fun setVM() {
        vm.state.observe(this, {
            when (it?.stepState) {
                HomeViewModelStepState.IDLE -> {
                    bwLogoContainer.visibility = View.VISIBLE
                    sendOtpContainer.visibility = View.GONE
                    verifyOtpContainer.visibility = View.GONE
                }
                HomeViewModelStepState.SEND_OTP -> {
                    bwLogoContainer.visibility = View.GONE
                    sendOtpContainer.visibility = View.VISIBLE
                    verifyOtpContainer.visibility = View.GONE
                }
                HomeViewModelStepState.CHECKING_TOKEN -> {
                    bwLogoContainer.visibility = View.GONE
                    sendOtpContainer.visibility = View.GONE
                    verifyOtpContainer.visibility = View.GONE
                }
                HomeViewModelStepState.VERIFY_OTP -> {
                    bwLogoContainer.visibility = View.GONE
                    sendOtpContainer.visibility = View.GONE
                    verifyOtpContainer.visibility = View.VISIBLE
                }
                HomeViewModelStepState.JUMP_TO_PWA -> {
                    bwLogoContainer.visibility = View.GONE
                    sendOtpContainer.visibility = View.GONE
                    verifyOtpContainer.visibility = View.GONE

                    val sharedPref = PreferenceHelper.defaultPrefs(this)
                    val token = sharedPref[KEnum.Companion.SharedPref.Token.name, ""]
                    val refreshToken = sharedPref[KEnum.Companion.SharedPref.RefreshToken.name, ""]
                    val userId = sharedPref[KEnum.Companion.SharedPref.UserId.name, ""]
                    val intent = PwaActivity.getIntent(
                            this,
                            token = token!!,
                            refreshToken = refreshToken!!,
                            userId = userId!!,
                            countryCode = "+62"
                    )
                    startActivity(intent)

                    bwLogoContainer.visibility = View.VISIBLE
                }
            }

            when (it?.state) {
                HomeViewModelState.IDLE -> {
                    loading.visibility = View.GONE
                    error.visibility = View.GONE
                }
                HomeViewModelState.LOADING -> {
                    loading.visibility = View.VISIBLE
                    bwLogoContainer.visibility = View.GONE
                    sendOtpContainer.visibility = View.GONE
                    verifyOtpContainer.visibility = View.GONE
                }
                HomeViewModelState.ERROR -> {
                    loading.visibility = View.GONE
                    error.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun setUI() {
        bwLogo.setOnClickListener {
            vm.initialize()
        }
        phone_edit_text.addTextChangedListener {
            vm.onPhoneChanged(it.toString())
        }
        otp_edit_text.addTextChangedListener {
            vm.onOTPChanged(it.toString())
        }
        send_otp_btn.setOnClickListener {
            vm.sendOtp()
        }
        verify_otp_btn.setOnClickListener {
            vm.login()
        }
    }


}

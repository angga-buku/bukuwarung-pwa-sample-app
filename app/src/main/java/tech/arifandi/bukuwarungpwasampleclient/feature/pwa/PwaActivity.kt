package tech.arifandi.bukuwarungpwasampleclient.feature.pwa

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_pwa.*
import tech.arifandi.bukuwarungpwasampleclient.Constants
import tech.arifandi.bukuwarungpwasampleclient.R

internal class JsObject(
        private val pwaActivity: PwaActivity,
) {

    @JavascriptInterface
    open fun start(token: String) {
        pwaActivity.syncPWA();
    }

    @JavascriptInterface
    override fun toString(): String {
        return "injectedObject"
    }
}

class PwaActivity : AppCompatActivity() {

    private lateinit var token: String
    private lateinit var refreshToken: String
    private lateinit var userId: String
    private lateinit var countryCode: String

    private val isNetworkAvailable: Boolean
        private get() {
            val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = manager.activeNetworkInfo
            var isAvailable = false
            if (networkInfo != null && networkInfo.isConnected) {
                // Wifi or Mobile Network is present and connected
                isAvailable = true
            }
            return isAvailable
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Setup Theme
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pwa)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "BukuWarung"

        val previousIntent = intent
        token = previousIntent.getStringExtra(TOKEN_KEY) ?: ""
        refreshToken = previousIntent.getStringExtra(REFRESH_TOKEN_KEY) ?: ""
        userId = previousIntent.getStringExtra(USER_ID_KEY) ?: ""
        countryCode = previousIntent.getStringExtra(COUNTRY_CODE_KEY) ?: ""

        // Setup App
        setupWebView()
        loadHome()
    }

    // handle load errors
    private fun handleLoadError(errorCode: Int) {
        // Unsupported Scheme, recover
        Handler().postDelayed({ onBackPressed() }, 100)
    }

    // handles initial setup of webview
    private fun setupWebView() {
        val webSettings = webview.settings
        // accept cookies
        CookieManager.getInstance().setAcceptCookie(true)
        // enable JS
        webSettings.javaScriptEnabled = true
        // must be set for our js-popup-blocker:
        webSettings.setSupportMultipleWindows(true)

        // PWA settings
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            webSettings.databasePath = applicationContext.filesDir.absolutePath
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            webSettings.setAppCacheMaxSize(Long.MAX_VALUE)
        }
        webSettings.domStorageEnabled = true
        webSettings.setAppCachePath(applicationContext.cacheDir.absolutePath)
        webSettings.setAppCacheEnabled(true)
        webSettings.databaseEnabled = true

        // enable mixed content mode conditionally
        if (Constants.ENABLE_MIXED_CONTENT
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
        }

        // retrieve content from cache primarily if not connected
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT

        // set User Agent
        if (Constants.OVERRIDE_USER_AGENT || Constants.POSTFIX_USER_AGENT) {
            var userAgent = webSettings.userAgentString
            if (Constants.OVERRIDE_USER_AGENT) {
                userAgent = Constants.USER_AGENT
            }
            if (Constants.POSTFIX_USER_AGENT) {
                userAgent = userAgent + " " + Constants.USER_AGENT_POSTFIX
            }
            webSettings.userAgentString = userAgent
        }

        // enable HTML5-support
        webview.webChromeClient = object : WebChromeClient() {
            //simple yet effective redirect/popup blocker
            override fun onCreateWindow(view: WebView, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message): Boolean {
                val href = view.handler.obtainMessage()
                view.requestFocusNodeHref(href)
                val popupUrl = href.data.getString("url")
                if (popupUrl != null) {
                    //it's null for most rouge browser hijack ads
                    webview.loadUrl(popupUrl)
                    return true
                }
                return false
            }
        }

        // Set up Webview client
        webview.webViewClient = object : WebViewClient() {
            // handle loading error by showing the offline screen
            @Deprecated("")
            override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    handleLoadError(errorCode)
                }
            }

            @TargetApi(Build.VERSION_CODES.M)
            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // new API method calls this on every error for each resource.
                    // we only want to interfere if the page itself got problems.
                    val url = request.url.toString()
                    if (view.url == url) {
                        handleLoadError(error.errorCode)
                    }
                }
            }
        }

        webview.addJavascriptInterface(JsObject(this), "BukuWarungPWA")
    }

    fun syncPWA() {
        webview.post {
            val script = "sendToken('$token', '$refreshToken', '$userId', '$countryCode');"
            webview.evaluateJavascript(script, null)
        }
    }

    private fun loadHome() {
        webview.loadUrl(Constants.WEBAPP_URL)
    }

    override fun onPause() {
        webview.onPause()
        super.onPause()
    }

    override fun onResume() {
        webview.onResume()
        super.onResume()
    }

    // Handle back-press in browser
    override fun onBackPressed() {
        super.onBackPressed()
        if (webview.canGoBack()) {
            webview.goBack()
        } else {
            super.onBackPressed()
        }
    }

    companion object {

        fun getIntent(
                context: Context,
                token: String,
                refreshToken: String,
                userId: String,
                countryCode: String,
            ): Intent {
            val intent = Intent(context, PwaActivity::class.java)
            intent.putExtra(TOKEN_KEY, token)
            intent.putExtra(REFRESH_TOKEN_KEY, refreshToken)
            intent.putExtra(USER_ID_KEY, userId)
            intent.putExtra(COUNTRY_CODE_KEY, countryCode)
            return intent
        }

        const val TOKEN_KEY = "TOKEN_KEY"
        const val REFRESH_TOKEN_KEY = "REFRESH_TOKEN_KEY"
        const val USER_ID_KEY = "USER_ID_KEY"
        const val COUNTRY_CODE_KEY = "COUNTRY_CODE_KEY"
    }

}
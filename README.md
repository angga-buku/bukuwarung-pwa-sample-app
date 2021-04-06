# Sample App for BukuWarung PWA Integration

# Legends
1. Prerequisite
2. Authentication Flow
3. How to integrate

# Prerequisite
To run this project you must have:
- An Android app capable to run WebView
- Client ID and client secret from BukuWarung team (ask your partnership PIC)
- BukuWarung's Authentication flow docs (if you want your Backend to proxy the calls)

# Authentication Flow
This flow is used to get these data needed for the PWA to launch:
- Authentication token
- Refresh Token
- User's phone number
- User's country code (most likely it'll be +62 for Indonesian users)

The steps are:
1. You need to store the tokens and data either in the shared preferences or in your Backend system
2a. IF there's already a token in your system, then you need to check if it's still valid, by substracting the time of login with now's time (token validity is 15 minutes)
2aa. IF it's still valid, then continue to the steps below
2ab. Else, you need to refresh the current token to get a new one (with new validity)
2b. Else, if there's no token in your system, meaning the user haven't logged on to BukuWarung from your app, you need to continue follow this steps
3. You need to show user the phone number field (and country code field if neccessary)
4. After user fill the phone number, you need to hit the SEND_OTP API from BukuWarung API to send OTP to user's phone number
5. After done with that, you need to show user the OTP field
6. After user fill the otp, you need to hit the LOGIN API from BukuWarung API to validate OTP sent from previous flow
7. If that's done, you'll get a token and refresh token. You can use that data for connecting to our PWA.

# How to integrate
1. Make sure all the prerequisite is fulfilled
2. Before launching PWA, you have to make sure the app has access to these data:
- Authentication token
- Refresh Token
- User's phone number
- User's country code (most likely it'll be +62 for Indonesian users)
3a. If not then go to Authentication flow to get it
3b. Else, create a Webview activity inside your app with a Javascript interface loaded in it, named "BukuWarungPWA". We communicate between PWA and Webview with this Javascript interface.
4. Implement this methods on the interface:
```
internal class JsObject(
        private val pwaActivity: PwaActivity,
) {

    @JavascriptInterface
    open fun start(token: String) {
        pwaActivity.syncPWA();
    }
}
```
5. then tie the interface to WebView with this code
```
webview.addJavascriptInterface(JsObject(this), "BukuWarungPWA")
```
6. Note that the `start` method on the interface will be called once the PWA is fully loaded and is on the Login page
7. Once PWA is fully loaded, we call this method to send your datas (tokens, phone, country code) to PWA:
```
fun syncPWA() {
        webview.post {
            val script = "sendToken('$token', '$refreshToken', '$userId', '$countryCode', '$primaryColorHex');"
            webview.evaluateJavascript(script, null)
        }
}
```
9. After that's done, then the PWA will automatically logged in to the account with given credentials.

package tech.arifandi.bukuwarungpwasampleclient.shared.k

class KCredential {
    companion object {
        const val Url = "https://api-staging.bukuwarung.com/api/v2/"
        // TODO: CHANGE WITH YOUR CLIENT ID
        const val ClientId = "..."
        // TODO: CHANGE WITH YOUR CLIENT SECRET
        const val ClientSecret = "..."
        const val TokenExpirationInMillis = 15 * 60000 // 15 minutes
        const val appVersionName = "3.3.5"
        const val appVersionCode = "3300"
    }
}
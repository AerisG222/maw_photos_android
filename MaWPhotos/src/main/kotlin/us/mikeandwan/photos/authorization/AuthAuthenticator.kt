package us.mikeandwan.photos.authorization

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationService
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber
import us.mikeandwan.photos.domain.AuthorizationRepository
import java.io.IOException

// https://www.coinbase.com/blog/okhttp-and-oauth-token-refreshes
@Suppress("UNUSED_ANONYMOUS_PARAMETER")
class AuthAuthenticator(
    private val authService: AuthService,
    private val authorizationService: AuthorizationService,
    private val authorizationRepository: AuthorizationRepository
) : Authenticator {
    @Synchronized
    @Throws(IOException::class)
    override fun authenticate(route: Route?, response: Response): Request? {
        val authState = authorizationRepository.authState.value
        val currToken = authState.accessToken
        var request: Request? = null

        Timber.i("authentication called for ${route?.address?.url}")
        Timber.d("authenticate (auth token): $currToken")
        Timber.d("authenticate (refresh token): ${authState.refreshToken}")

        if (authState.refreshToken == null) {
            Timber.i("refresh token is null, will not try to refresh")
            return request
        }

        Timber.d("Starting Authenticator.authenticate")

        val potentiallyNewToken = authorizationRepository.authState.value.accessToken

        // see if prior attempt included an auth token
        if (response.request.header("Authorization") != null) {
            // see if token was already refreshed, and if so try that
            if (potentiallyNewToken != currToken) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $potentiallyNewToken")
                    .build()
            }
        }

        try {
            authState.performActionWithFreshTokens(authorizationService) {
                accessToken: String?,
                idToken: String?,
                ex: AuthorizationException? ->

                Timber.i("perform with fresh tokens called for ${route?.address?.url}")

                when {
                    ex != null -> Timber.e(ex, "Failed to authorize")
                    accessToken == null -> Timber.e("Failed to authorize, received null access token")
                    else -> {
                        Timber.i("authenticate: obtained access token")

                        runBlocking {
                            authorizationRepository.save(authState)
                        }

                        request = response.request.newBuilder()
                            .header("Authorization", "Bearer $accessToken")
                            .build()
                    }
                }

                // if we don't have a new request to try and refresh the auth, logout to forcefully
                // signal that a user will be required to login
                if(request == null) {
                    runBlocking {
                        CoroutineScope(Dispatchers.IO).launch {
                            authService.logout()
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            Timber.e(ex, "Failed to renew tokens")
        }

        return request
    }
}

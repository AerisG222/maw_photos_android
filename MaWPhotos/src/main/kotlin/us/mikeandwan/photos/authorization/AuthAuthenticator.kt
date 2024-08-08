package us.mikeandwan.photos.authorization

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
        var request: Request? = null

        Timber.i("authenticate called for ${route?.address?.url}")
        Timber.d("authenticate (current access token): ${authState.accessToken}")
        Timber.d("authenticate (current refresh token): ${authState.refreshToken}")

        if (authState.refreshToken == null) {
            Timber.i("refresh token is null, will not try to refresh")
            return request
        }

        try {
            authState.performActionWithFreshTokens(authorizationService) {
                accessToken: String?,
                idToken: String?,
                ex: AuthorizationException? ->

                Timber.i("perform with fresh tokens called for ${route?.address?.url}")
                Timber.d("authenticate (post access token): $accessToken")

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
                        authService.logout()
                    }
                }
            }
        } catch (ex: Exception) {
            Timber.e(ex, "Failed to renew tokens")
        }

        return request
    }
}

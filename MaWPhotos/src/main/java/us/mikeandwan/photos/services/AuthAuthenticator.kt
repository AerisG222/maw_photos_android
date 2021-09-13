package us.mikeandwan.photos.services

import kotlinx.coroutines.*
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationService
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber
import java.io.IOException

class AuthAuthenticator(
    private val _authService: AuthorizationService,
    private val _authStateManager: AuthStateManager
) : Authenticator {
    @Synchronized
    @Throws(IOException::class)
    override fun authenticate(route: Route?, response: Response): Request? {
        val authState = _authStateManager.current
        var request: Request? = null

        Timber.d("Starting Authenticator.authenticate")

        runBlocking {
            launch {
                authState.performActionWithFreshTokens(_authService) { accessToken: String?, idToken: String?, ex: AuthorizationException? ->
                    if (ex != null) {
                        Timber.e("Failed to authorize = %s", ex.message)

                        request = null
                    } else if (accessToken == null) {
                        Timber.e("Failed to authorize, received null access token")

                        request = null // Give up, we've already failed to authenticate.
                    } else {
                        Timber.i("authenticate: obtained access token")

                        request = response.request().newBuilder()
                            .header("Authorization", String.format("Bearer %s", accessToken))
                            .build()
                    }
                }
            }
        }

        return request
    }
}
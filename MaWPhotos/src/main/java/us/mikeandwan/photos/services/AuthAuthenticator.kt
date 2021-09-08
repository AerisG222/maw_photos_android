package us.mikeandwan.photos.services

import java9.util.concurrent.CompletableFuture
import net.openid.appauth.AuthorizationService
import us.mikeandwan.photos.services.AuthStateManager
import kotlin.jvm.Synchronized
import kotlin.Throws
import okhttp3.Route
import net.openid.appauth.AuthState
import timber.log.Timber
import net.openid.appauth.AuthState.AuthStateAction
import net.openid.appauth.AuthorizationException
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.lang.Exception

class AuthAuthenticator(
    private val _authService: AuthorizationService,
    private val _authStateManager: AuthStateManager
) : Authenticator {
    @Synchronized
    @Throws(IOException::class)
    override fun authenticate(route: Route?, response: Response): Request? {
        val future = CompletableFuture<Request?>()
        val authState = _authStateManager.current
        Timber.d("Starting Authenticator.authenticate")
        authState.performActionWithFreshTokens(_authService) { accessToken: String?, idToken: String?, ex: AuthorizationException? ->
            if (ex != null) {
                Timber.e("Failed to authorize = %s", ex.message)
                future.complete(null)
            } else if (accessToken == null) {
                Timber.e("Failed to authorize, received null access token")
                future.complete(null) // Give up, we've already failed to authenticate.
            } else {
                Timber.i("authenticate: obtained access token")
                val request = response.request().newBuilder()
                    .header("Authorization", String.format("Bearer %s", accessToken))
                    .build()
                future.complete(request)
            }
        }
        return try {
            future.get()
        } catch (ex: Exception) {
            Timber.e("Error: %s", ex.message)
            null
        }
    }
}
package us.mikeandwan.photos.authorization

import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationService
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber
import us.mikeandwan.photos.domain.AuthorizationRepository
import java.io.IOException

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
class AuthAuthenticator(
    private val authService: AuthorizationService,
    private val authorizationRepository: AuthorizationRepository
) : Authenticator {
    @Synchronized
    @Throws(IOException::class)
    override fun authenticate(route: Route?, response: Response): Request? {
        val authState = authorizationRepository.authState.value
        var request: Request? = null

        Timber.d("Starting Authenticator.authenticate")

        authState.performActionWithFreshTokens(authService) {
            accessToken: String?,
            idToken: String?,
            ex: AuthorizationException? ->

            when {
                ex != null -> Timber.e(ex, "Failed to authorize")
                accessToken == null -> Timber.e("Failed to authorize, received null access token")
                else -> {
                    Timber.i("authenticate: obtained access token")
                    request = response.request.newBuilder()
                        .header("Authorization", "Bearer $accessToken")
                        .build()
                }
            }
        }

        return request
    }
}

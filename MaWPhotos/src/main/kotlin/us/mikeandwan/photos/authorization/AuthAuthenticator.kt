package us.mikeandwan.photos.authorization

import kotlinx.coroutines.runBlocking
import net.openid.appauth.AuthorizationService
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber
import us.mikeandwan.photos.domain.AuthorizationRepository

// https://www.coinbase.com/blog/okhttp-and-oauth-token-refreshes
class AuthAuthenticator(
    private val authorizationService: AuthorizationService,
    private val authorizationRepository: AuthorizationRepository
) : Authenticator {
    @Synchronized
    override fun authenticate(route: Route?, response: Response): Request? {
        val authState = authorizationRepository.authState.value
        var request: Request? = null

        Timber.i("authenticate: ${route?.address?.url}")
        Timber.d("authenticate (current access token): ${authState.accessToken}")
        Timber.d("authenticate (current refresh token): ${authState.refreshToken}")

        try {
            authState.performActionWithFreshTokens(authorizationService) { newAccessToken, _, authException ->
                Timber.i("performActionWithFreshTokens: ${route?.address?.url}")
                Timber.d("performActionWithFreshTokens (new access token): $newAccessToken")

                when {
                    authException != null -> Timber.e(authException, "Failed to authorize")
                    newAccessToken == null -> Timber.e("Failed to authorize, received null access token")
                    else -> {
                        Timber.i("authenticate: obtained access token")

                        runBlocking {
                            authorizationRepository.save(authState)
                        }

                        request = response.request.newBuilder()
                            .header("Authorization", "Bearer $newAccessToken")
                            .build()
                    }
                }
            }
        } catch (ex: Exception) {
            Timber.e(ex, "Failed to renew tokens")
        }

        return request
    }
}

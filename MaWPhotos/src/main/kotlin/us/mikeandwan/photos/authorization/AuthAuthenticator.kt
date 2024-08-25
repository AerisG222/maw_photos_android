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

        Timber.i("authenticate: ${route?.address?.url} || ${authState.accessToken} || ${authState.refreshToken}")

        try {
            authState.performActionWithFreshTokens(authorizationService) { newAccessToken, _, authException ->
                if(authException != null) {
                    throw authException
                }

                if(newAccessToken == null) {
                    throw Exception("Failed to authorize, received null access token")
                }

                Timber.i("performActionWithFreshTokens: ${route?.address?.url} || $newAccessToken")

                runBlocking {
                    authorizationRepository.save(authState)
                }

                request = response.request.newBuilder()
                    .header("Authorization", "Bearer $newAccessToken")
                    .build()
            }
        } catch (ex: Exception) {
            Timber.e(ex, "Failed to renew tokens: ${route?.address?.url} || ${ex.message}")
        }

        return request
    }
}

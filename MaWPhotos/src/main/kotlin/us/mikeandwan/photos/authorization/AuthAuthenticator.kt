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
    private val authService: AuthService,
    private val authorizationService: AuthorizationService,
    private val authorizationRepository: AuthorizationRepository
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        // get original token - if none exists, return null as we won't be able to get a new one
        val origAuthHeader = response.request.headers["Authorization"] ?: return null
        val origToken = origAuthHeader.replace("Bearer ", "")

        Timber.i("authenticate: ${route?.address?.url} || $origToken")

        synchronized(this) {
            val authState = authorizationRepository.authState.value
            val currToken = authState.accessToken
            var request: Request? = null

            // if we got a new token in a separate attempt/call/thread, try that one first
            if(currToken != null && origToken != currToken) {
                request = buildRequest(response, currToken)
            }
            else {
                authState.performActionWithFreshTokens(authorizationService) { newAccessToken, _, authException ->
                    try {
                        if (authException != null) {
                            throw authException
                        }

                        if (newAccessToken == null) {
                            throw Exception("Failed to authorize, received null access token")
                        }

                        Timber.i("performActionWithFreshTokens: ${route?.address?.url} || $newAccessToken")

                        runBlocking {
                            authorizationRepository.save(authState)
                        }

                        request = buildRequest(response, newAccessToken)
                    } catch (ex: Exception) {
                        Timber.e(ex, "Failed to renew tokens: ${route?.address?.url} || ${ex.message}")

                        // if we don't have a new request to try and refresh the auth, logout to forcefully
                        // signal that a user will be required to login
                        runBlocking {
                            authService.logout()
                        }
                    }
                }
            }

            return request
        }
    }

    fun buildRequest(response: Response, accessToken: String): Request {
        return response.request.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()
    }
}

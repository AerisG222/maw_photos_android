package us.mikeandwan.photos.authorization

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.first
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

        Timber.i("authenticate: ${response.request.url} || ${trimToken(origToken)}")

        synchronized(this) {
            return runBlocking {
                val tokenFlow = Channel<String?>()
                val tf = tokenFlow.consumeAsFlow()
                val authState = authorizationRepository.authState.value
                val currToken = authState.accessToken

                Timber.i("inner authenticate: ${response.request.url} || ${trimToken(origToken)}")

                // if we got a new token in a separate attempt/call/thread, try that one first
                if (currToken != null && origToken != currToken) {
                    Timber.i("token already updated!")
                    tokenFlow.send(currToken)
                } else {
                    authState.performActionWithFreshTokens(authorizationService) { newAccessToken, _, authException ->
                        try {
                            if (authException != null) {
                                throw authException
                            }

                            if (newAccessToken == null) {
                                throw Exception("Failed to authorize, received null access token")
                            }

                            Timber.i("performActionWithFreshTokens: ${route?.address?.url} || ${trimToken(newAccessToken)}")

                            runBlocking {
                                authorizationRepository.save(authState)
                                tokenFlow.send(newAccessToken)
                            }
                        } catch (ex: Exception) {
                            Timber.e(ex, "Failed to renew tokens: ${response.request.url} || ${ex.message}")

                            // if we don't have a new request to try and refresh the auth, logout to forcefully
                            // signal that a user will be required to login
                            runBlocking {
                                authService.logout()
                                tokenFlow.send(null)
                            }
                        }
                    }
                }

                val token =  tf.first()

                return@runBlocking token?.let { buildRequest(response, it) }
            }
        }
    }

    fun buildRequest(response: Response, accessToken: String): Request {
        return response.request.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()
    }

    fun trimToken(token: String) = token.replaceRange(5, token.length - 5, "...")
}

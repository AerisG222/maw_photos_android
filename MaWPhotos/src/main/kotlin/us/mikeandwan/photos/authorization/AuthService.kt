package us.mikeandwan.photos.authorization

import android.app.Application
import android.content.Intent
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import net.openid.appauth.*
import net.openid.appauth.AuthorizationServiceConfiguration.fetchFromIssuer
import timber.log.Timber
import us.mikeandwan.photos.Constants
import us.mikeandwan.photos.R
import us.mikeandwan.photos.domain.AuthorizationRepository
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import androidx.core.net.toUri

// inspired by: https://curity.io/resources/learn/kotlin-android-appauth/
class AuthService(
    application: Application,
    private val authorizationService: AuthorizationService,
    private val authorizationRepository: AuthorizationRepository
) {
    private val authClientId: String = application.resources.getString(R.string.auth_client_id)
    private val authSchemeRedirect: String = application.resources.getString(R.string.auth_scheme_redirect_uri)
    private val authSchemeRedirectUri: Uri = authSchemeRedirect.toUri()

    val authStatus = authorizationRepository.authState
        .map {
            when {
                it.isAuthorized -> AuthStatus.Authorized
                it.refreshToken != null -> AuthStatus.Authorized
                else -> AuthStatus.RequiresAuthorization
            }
        }

    suspend fun logout() {
       authorizationRepository.save(null)
    }

    suspend fun loadConfig(): AuthorizationServiceConfiguration {
        return withContext(Dispatchers.IO) {
            return@withContext suspendCoroutine { continuation ->
                fetchFromIssuer(Constants.AUTH_BASE_URL.toUri()) { metadata, ex ->
                    when {
                        metadata != null -> {
                            Timber.i("metadata retrieved successfully")
                            Timber.d(metadata.toJsonString())
                            continuation.resume(metadata)
                        }

                        else -> {
                            val error = createAuthorizationError(
                                "failed to fetch openidc configuration",
                                ex
                            )
                            continuation.resumeWithException(error)
                        }
                    }
                }
            }
        }
    }

    fun getAuthorizationRedirectIntent(metadata: AuthorizationServiceConfiguration): Intent {
        val request = AuthorizationRequest.Builder(
            metadata,
            authClientId,
            ResponseTypeValues.CODE,
            authSchemeRedirectUri
        )
            .setScope("openid offline_access profile email role maw_api")
            .build()

        return authorizationService.getAuthorizationRequestIntent(request)
    }

    fun completeAuthorization(
        response: AuthorizationResponse?,
        ex: AuthorizationException?
    ) : AuthorizationResponse {
        if (response == null) {
            throw createAuthorizationError("Authorization Request Error", ex)
        }

        Timber.i("Authorization response received successfully")
        Timber.d("CODE: ${response.authorizationCode}, STATE: ${response.state}")

        return response
    }

    suspend fun redeemCodeForTokens(authResponse: AuthorizationResponse): TokenResponse? {
        return suspendCoroutine { continuation ->
            val extraParams = mutableMapOf<String, String>()
            val tokenRequest = authResponse.createTokenExchangeRequest(extraParams)

            authorizationService.performTokenRequest(tokenRequest) { tokenResponse, ex ->
                when {
                    tokenResponse != null -> {
                        Timber.i("Authorization code grant response received successfully")
                        Timber.d("AT: ${tokenResponse.accessToken}, RT: ${tokenResponse.refreshToken}, IDT: ${tokenResponse.idToken}" )
                        continuation.resume(tokenResponse)
                    }
                    else -> {
                        val error = createAuthorizationError("Authorization Response Error", ex)
                        continuation.resumeWithException(error)
                    }
                }
            }
        }
    }

    private fun createAuthorizationError(title: String, ex: AuthorizationException?): ServerCommunicationException {
        val parts = mutableListOf<String>()

        if (ex?.type != null) {
            parts.add("(${ex.type} / ${ex.code})")
        }

        if (ex?.error != null) {
            parts.add(ex.error!!)
        }

        val description: String = if (ex?.errorDescription != null) {
            ex.errorDescription!!
        } else {
            "Unknown Error"
        }
        parts.add(description)

        val fullDescription = parts.joinToString(" : ")
        Timber.e(fullDescription)

        return ServerCommunicationException(title, fullDescription)
    }
}

class ServerCommunicationException(
    errorTitle: String,
    errorDescription: String?) : ApplicationException(errorTitle, errorDescription)

open class ApplicationException(val errorTitle: String,
                                val errorDescription: String?) : RuntimeException()

class InvalidIdTokenException(errorDescription: String) :
    ApplicationException("Invalid ID Token", errorDescription)

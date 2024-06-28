package us.mikeandwan.photos.authorization

import androidx.annotation.AnyThread
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.TokenResponse
import org.json.JSONException
import timber.log.Timber
import us.mikeandwan.photos.database.Authorization
import us.mikeandwan.photos.database.AuthorizationDao
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

// based on: https://github.com/openid/AppAuth-Android/blob/master/app/java/net/openid/appauthdemo/AuthStateManager.java
class AuthStateManager(
    private val authorizationDao: AuthorizationDao
) {
    private val lock: ReentrantLock = ReentrantLock()
    private val currentAuthState: AtomicReference<AuthState> = AtomicReference()

    val current: AuthState
        @AnyThread get() {
            currentAuthState.get()?.let { return it }

            val state = readState()

            return if (currentAuthState.compareAndSet(null, state)) {
                state
            } else {
                currentAuthState.get()
            }
        }

    @AnyThread
    fun replace(state: AuthState): AuthState {
        writeState(state)
        currentAuthState.set(state)

        return state
    }

    @AnyThread
    fun updateAfterAuthorization(
        response: AuthorizationResponse?,
        ex: AuthorizationException?
    ): AuthState = updateState { it.update(response, ex) }

    @AnyThread
    fun updateAfterTokenResponse(
        response: TokenResponse?,
        ex: AuthorizationException?
    ): AuthState = updateState { it.update(response, ex) }

    @AnyThread
    private fun updateState(updateAction: (AuthState) -> Unit): AuthState {
        val currentState = current
        updateAction(currentState)

        return replace(currentState)
    }

    @AnyThread
    private fun readState(): AuthState {
        lock.withLock {
            var authState = AuthState()

            runBlocking {
                val currentState = authorizationDao.getAuthorization(AUTHORIZATION_ID).firstOrNull()

                if(currentState != null) {
                    try {
                        authState = AuthState.jsonDeserialize(currentState.json)
                    } catch (ex: JSONException) {
                        Timber.w("Failed to deserialize stored auth state - discarding")
                    }
                }
            }

            return authState
        }
    }

    @AnyThread
    private fun writeState(state: AuthState?) {
        lock.withLock {
            runBlocking {
                if(state == null) {
                    authorizationDao.deleteAuthorization(AUTHORIZATION_ID)
                } else {
                    val auth = Authorization(AUTHORIZATION_ID, state.jsonSerializeString())

                    authorizationDao.setAuthorization(auth)
                }
            }
        }
    }

    companion object {
        private const val AUTHORIZATION_ID = 1
    }
}

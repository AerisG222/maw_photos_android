package us.mikeandwan.photos.authorization

import androidx.annotation.AnyThread
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import net.openid.appauth.*
import org.json.JSONException
import timber.log.Timber
import us.mikeandwan.photos.database.Authorization
import us.mikeandwan.photos.database.AuthorizationDao
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock

// based on: https://github.com/openid/AppAuth-Android/blob/master/app/java/net/openid/appauthdemo/AuthStateManager.java
class AuthStateManager constructor(
    private val authorizationDao: AuthorizationDao
) {
    private val _lock: ReentrantLock = ReentrantLock()
    private val mCurrentAuthState: AtomicReference<AuthState> = AtomicReference()

    @get:AnyThread
    val current: AuthState
        get() {
            if (mCurrentAuthState.get() != null) {
                return mCurrentAuthState.get()
            }

            val state = readState()

            return if (mCurrentAuthState.compareAndSet(null, state)) {
                state
            } else {
                mCurrentAuthState.get()
            }
        }

    @AnyThread
    fun replace(state: AuthState): AuthState {
        writeState(state)
        mCurrentAuthState.set(state)

        return state
    }

    @AnyThread
    fun updateAfterAuthorization(
        response: AuthorizationResponse?,
        ex: AuthorizationException?
    ): AuthState {
        val current = current
        current.update(response, ex)

        return replace(current)
    }

    @AnyThread
    fun updateAfterTokenResponse(
        response: TokenResponse?,
        ex: AuthorizationException?
    ): AuthState {
        val current = current
        current.update(response, ex)

        return replace(current)
    }

    @AnyThread
    private fun readState(): AuthState {
        _lock.lock()

        return try {
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
        } finally {
            _lock.unlock()
        }
    }

    @AnyThread
    private fun writeState(state: AuthState?) {
        _lock.lock()

        try {
            runBlocking {
                if(state == null) {
                    authorizationDao.deleteAuthorization(AUTHORIZATION_ID)
                } else {
                    val auth = Authorization(AUTHORIZATION_ID, state.jsonSerializeString())

                    authorizationDao.setAuthorization(auth)
                }
            }
        } finally {
            _lock.unlock()
        }
    }

    companion object {
        private const val AUTHORIZATION_ID = 1
    }
}
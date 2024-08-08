package us.mikeandwan.photos.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import net.openid.appauth.AuthState
import us.mikeandwan.photos.database.Authorization
import us.mikeandwan.photos.database.AuthorizationDao
import javax.inject.Inject

class AuthorizationRepository @Inject constructor(
    private val dao: AuthorizationDao
) {
    companion object {
        private const val AUTHORIZATION_ID = 1
    }

    private val _authState = MutableStateFlow<AuthState>(getInitialAuthState())
    val authState = _authState.asStateFlow()

    fun getInitialAuthState(): AuthState {
        return runBlocking {
            dao
                .getAuthorization(AUTHORIZATION_ID)
                .map {
                    if (it == null) {
                        AuthState()
                    } else {
                        AuthState.jsonDeserialize(it.json)
                    }
                }
                .first()
        }
    }

    suspend fun save(state: AuthState?) {
        if(state == null) {
            _authState.update { AuthState() }
            dao.deleteAuthorization(AUTHORIZATION_ID)
        } else {
            // serialize and deserialize to get a new object so state flow sees this as a new value
            val json = state.jsonSerializeString()

            dao.setAuthorization(Authorization(AUTHORIZATION_ID, json))
            _authState.value = AuthState.jsonDeserialize(json)
        }
    }
}

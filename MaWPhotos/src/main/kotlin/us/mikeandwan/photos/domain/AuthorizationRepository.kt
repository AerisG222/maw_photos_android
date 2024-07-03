package us.mikeandwan.photos.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

    private val scope = CoroutineScope(Dispatchers.Main)

    val authState = dao
        .getAuthorization(AUTHORIZATION_ID)
        .map {
            if(it == null) {
                AuthState()
            } else {
                AuthState.jsonDeserialize(it.json)
            }
        }
        .stateIn(scope, WhileSubscribed(5000), AuthState())

    suspend fun save(state: AuthState?) {
        if(state == null) {
            dao.deleteAuthorization(AUTHORIZATION_ID)
        } else {
            dao.setAuthorization(Authorization(AUTHORIZATION_ID, state.jsonSerializeString()))
        }
    }
}

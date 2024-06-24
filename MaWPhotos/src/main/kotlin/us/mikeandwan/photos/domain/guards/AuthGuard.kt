package us.mikeandwan.photos.domain.guards

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import us.mikeandwan.photos.authorization.AuthService
import us.mikeandwan.photos.authorization.AuthStatus
import javax.inject.Inject

class AuthGuard @Inject constructor (
    private val authService: AuthService
) {
    val status = authService
        .authStatus
        .map { it ->
            if(it == AuthStatus.Authorized) {
                GuardStatus.Passed
            } else {
                GuardStatus.Failed
            }
        }
        .onStart { emit(GuardStatus.Unknown) }
}

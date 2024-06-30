package us.mikeandwan.photos.domain.guards

import kotlinx.coroutines.flow.map
import us.mikeandwan.photos.authorization.AuthService
import us.mikeandwan.photos.authorization.AuthStatus
import javax.inject.Inject

class AuthGuard @Inject constructor (
    authService: AuthService
) : IGuard {
    override val status = authService
        .authStatus
        .map {
            if(it == AuthStatus.Authorized) {
                GuardStatus.Passed
            } else {
                GuardStatus.Failed
            }
        }

    override fun initializeGuard() {
        // do nothing - state immediately available through auth service
    }
}

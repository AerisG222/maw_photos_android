package us.mikeandwan.photos.domain.guards

import kotlinx.coroutines.flow.map
import us.mikeandwan.photos.authorization.AuthService
import us.mikeandwan.photos.authorization.AuthStatus
import us.mikeandwan.photos.domain.AuthorizationRepository
import javax.inject.Inject

class AuthGuard @Inject constructor (
    authService: AuthService,
    private val authorizationRepository: AuthorizationRepository
) : IGuard {
    // we relax the check below to also see if they have a refresh token.  we would only have
    // a refresh token if they successfully logged in before.  this has the assumption that
    // the next attempt to request data will cause the refresh flow to run.
    override val status = authService
        .authStatus
        .map {
            if(
                it == AuthStatus.Authorized ||
                authorizationRepository.authState.value.refreshToken != null
            ) {
                GuardStatus.Passed
            } else {
                GuardStatus.Failed
            }
        }

    override fun initializeGuard() {
        // no additional initialization
    }
}

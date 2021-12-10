package us.mikeandwan.photos.authorization

sealed class AuthStatus constructor(val isAuthorized: Boolean) {
    class LoginInProcess: AuthStatus(false)
    class Completed(isAuthorized: Boolean): AuthStatus(isAuthorized)
}
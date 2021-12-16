package us.mikeandwan.photos.authorization

sealed class AuthStatus {
    object RequiresAuthorization: AuthStatus()
    object LoginInProcess: AuthStatus()
    object Authorized: AuthStatus()
}
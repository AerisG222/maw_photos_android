package us.mikeandwan.photos.domain.guards

sealed class GuardStatus {
    data object Unknown: GuardStatus()
    data object Passed: GuardStatus()
    data object Failed: GuardStatus()
}

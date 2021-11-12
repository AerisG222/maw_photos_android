package us.mikeandwan.photos.domain.models

data class NavigationInstruction (
    val actionId: Int?,
    val popBackId: Int?,
    val targetNavigationArea: NavigationArea
)
package us.mikeandwan.photos.domain

data class NavigationInstruction (
    val actionId: Int?,
    val popBackId: Int?,
    val targetNavigationArea: NavigationArea
)
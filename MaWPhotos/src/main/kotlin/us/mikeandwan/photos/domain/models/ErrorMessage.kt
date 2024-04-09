package us.mikeandwan.photos.domain.models

sealed class ErrorMessage {
    object DoNotDisplay: ErrorMessage()
    data class Display(val message: String): ErrorMessage()
}
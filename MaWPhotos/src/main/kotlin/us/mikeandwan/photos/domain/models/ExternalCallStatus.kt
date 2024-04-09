package us.mikeandwan.photos.domain.models

sealed class ExternalCallStatus<out T> {
    object Loading: ExternalCallStatus<Nothing>()
    data class Success<T>(val result: T): ExternalCallStatus<T>()
    data class Error(val message: String, val cause: Throwable? = null) : ExternalCallStatus<Nothing>()
}
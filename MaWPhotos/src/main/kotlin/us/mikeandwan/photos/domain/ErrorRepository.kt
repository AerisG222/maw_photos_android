package us.mikeandwan.photos.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import us.mikeandwan.photos.domain.models.ErrorMessage

class ErrorRepository {
    private val _error = MutableStateFlow<ErrorMessage>(ErrorMessage.DoNotDisplay)
    val error = _error.asStateFlow()

    fun showError(message: String) {
        _error.value = ErrorMessage.Display(message)
    }
}

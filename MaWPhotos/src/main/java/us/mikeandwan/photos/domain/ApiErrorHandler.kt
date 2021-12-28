package us.mikeandwan.photos.domain

import kotlinx.coroutines.CancellationException
import us.mikeandwan.photos.api.ApiResult
import us.mikeandwan.photos.authorization.AuthService
import us.mikeandwan.photos.domain.models.ExternalCallStatus
import us.mikeandwan.photos.ui.toExternalCallStatus
import javax.inject.Inject

class ApiErrorHandler @Inject constructor(
    private val authService: AuthService,
    private val errorRepository: ErrorRepository
) {
    fun handleError(error: ApiResult.Error, message: String?): ExternalCallStatus<Nothing> {
        if(error.exception is CancellationException) {
            return error.toExternalCallStatus()
        }

        if(error.isUnauthorized()) {
            authService.logout()
        } else {
            if(!message.isNullOrBlank()) {
                errorRepository.showError(message)
            }
        }

        return error.toExternalCallStatus()
    }

    fun handleEmpty(empty: ApiResult.Empty, message: String?): ExternalCallStatus<Nothing> {
        if(!message.isNullOrBlank()) {
            errorRepository.showError(message)
        }

        return empty.toExternalCallStatus()
    }
}
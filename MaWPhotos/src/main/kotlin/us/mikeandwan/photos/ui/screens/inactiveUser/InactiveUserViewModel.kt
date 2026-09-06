package us.mikeandwan.photos.ui.screens.inactiveUser

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import us.mikeandwan.photos.authorization.AuthService
import us.mikeandwan.photos.domain.ConfigRepository
import us.mikeandwan.photos.domain.ErrorRepository
import us.mikeandwan.photos.domain.models.UserStatus

data class InactiveUserUiState(
    val userStatus: UserStatus = UserStatus.Unknown,
    val isLoading: Boolean = false,
)

@HiltViewModel
class InactiveUserViewModel
    @Inject
    constructor(
        private val authService: AuthService,
        private val configRepository: ConfigRepository,
        private val errorRepository: ErrorRepository,
    ) : ViewModel() {
        // isLoading belongs to the button press rather than to anything upstream, so it is held
        // here and combined in rather than being pushed into the state from queryUserStatus
        private val _isLoading = MutableStateFlow(false)

        val uiState = combine(
            configRepository.userStatus,
            _isLoading,
        ) { status, isLoading ->
            InactiveUserUiState(userStatus = status, isLoading = isLoading)
        }.stateIn(viewModelScope, WhileSubscribed(5000), InactiveUserUiState())

        fun queryUserStatus() {
            viewModelScope.launch {
                _isLoading.update { true }
                configRepository.getUserStatus()
                _isLoading.update { false }

                if (configRepository.userStatus.value is UserStatus.Inactive) {
                    errorRepository.showThenClearError("Sorry, your account is still inactive.")
                }
            }
        }

        fun logout(context: Context) {
            viewModelScope.launch {
                authService.logout(context)
                configRepository.clearUserStatus()
            }
        }
    }

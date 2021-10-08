package us.mikeandwan.photos.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NavigationStateRepository {
    private val _closeSignal = MutableStateFlow(false)
    val closeSignal = _closeSignal.asStateFlow()

    fun requestClose() {
        _closeSignal.value = true
    }

    fun closeCompleted() {
        _closeSignal.value = false
    }
}
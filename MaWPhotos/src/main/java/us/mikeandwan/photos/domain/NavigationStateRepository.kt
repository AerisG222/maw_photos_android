package us.mikeandwan.photos.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NavigationStateRepository {
    private val _closeSignal = MutableStateFlow(false)
    val closeSignal = _closeSignal.asStateFlow()

    private val _toolbarTitle = MutableStateFlow("")
    val toolbarTitle = _toolbarTitle.asStateFlow()

    fun requestClose() {
        _closeSignal.value = true
    }

    fun closeCompleted() {
        _closeSignal.value = false
    }

    fun setToolbarTitle(title: String) {
        _toolbarTitle.value = title
    }
}
package us.mikeandwan.photos.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NavigationStateRepository {
    private val _closeNavDrawerSignal = MutableStateFlow(false)
    val closeNavDrawerSignal = _closeNavDrawerSignal.asStateFlow()

    private val _toolbarTitle = MutableStateFlow("")
    val toolbarTitle = _toolbarTitle.asStateFlow()

    fun requestNavDrawerClose() {
        _closeNavDrawerSignal.value = true
    }

    fun closeNavDrawerCompleted() {
        _closeNavDrawerSignal.value = false
    }

    fun setToolbarTitle(title: String) {
        _toolbarTitle.value = title
    }
}
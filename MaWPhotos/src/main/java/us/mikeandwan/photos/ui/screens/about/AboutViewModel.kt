package us.mikeandwan.photos.ui.screens.about

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import us.mikeandwan.photos.BuildConfig
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor() : ViewModel() {
    val version = "v${BuildConfig.VERSION_NAME}"

    private val _history = MutableStateFlow("")
    val history = _history.asStateFlow()

    fun setHistory(history: String) {
        _history.value = history
    }
}
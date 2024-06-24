package us.mikeandwan.photos.ui.screens.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import us.mikeandwan.photos.BuildConfig
import us.mikeandwan.photos.MawApplication
import us.mikeandwan.photos.R
import javax.inject.Inject

sealed class AboutState {
    data object Unknown : AboutState()
    data class Valid(
        val version: String,
        val history: String
    )
}

@HiltViewModel
class AboutViewModel @Inject constructor() : ViewModel() {
    private val version = "v${BuildConfig.VERSION_NAME}"
    private val _history = MutableStateFlow("")

    val state = _history
        .map { history ->
            if(history.isEmpty()) {
                AboutState.Unknown
            } else {
                AboutState.Valid(version, history)
            }
        }
        .stateIn(viewModelScope, WhileSubscribed(5000), AboutState.Unknown)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _history.value = MawApplication.instance.resources
                .openRawResource(R.raw.release_notes)
                .bufferedReader()
                .use { it.readText() }
        }
    }
}

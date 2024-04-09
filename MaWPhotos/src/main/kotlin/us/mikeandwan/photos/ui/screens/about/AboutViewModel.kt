package us.mikeandwan.photos.ui.screens.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import us.mikeandwan.photos.BuildConfig
import us.mikeandwan.photos.MawApplication
import us.mikeandwan.photos.R
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor() : ViewModel() {
    val version = "v${BuildConfig.VERSION_NAME}"

    private val _history = MutableStateFlow("")
    val history = _history.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _history.value = MawApplication.instance.resources
                .openRawResource(R.raw.release_notes)
                .bufferedReader()
                .use { it.readText() }
        }
    }
}
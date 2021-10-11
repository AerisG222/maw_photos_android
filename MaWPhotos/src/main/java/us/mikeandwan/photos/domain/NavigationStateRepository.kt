package us.mikeandwan.photos.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import us.mikeandwan.photos.R
import javax.inject.Inject

class NavigationStateRepository @Inject constructor(
    private val activeIdRepository: ActiveIdRepository,
    private val photoCategoryRepository: PhotoCategoryRepository
) {
    private val _closeNavDrawerSignal = MutableStateFlow(false)
    val closeNavDrawerSignal = _closeNavDrawerSignal.asStateFlow()

    private val _toolbarTitle = MutableStateFlow("")
    val toolbarTitle = _toolbarTitle.asStateFlow()

    private val _enableDrawer = MutableStateFlow(true)
    val enableDrawer = _enableDrawer.asStateFlow()

    suspend fun onDestinationChanged(destinationId: Int) {
        when(destinationId) {
            R.id.navigation_about -> {
                disableDrawer()
                setToolbarTitle("About")
            }
            R.id.navigation_categories -> {
                val year = activeIdRepository.getActivePhotoCategoryYear().first()?.toString() ?: ""

                enableDrawer()
                setToolbarTitle(year)
            }
            R.id.navigation_settings -> {
                disableDrawer()
                setToolbarTitle("Settings")
            }
            R.id.navigation_random -> {
                enableDrawer()
                setToolbarTitle("Random")
            }
            R.id.navigation_photos -> {
                enableDrawer()
                setToolbarTitle(photoCategoryRepository.getCategory().first().name)
            }
        }
    }

    fun setToolbarTitle(title: String) {
        _toolbarTitle.value = title
    }

    fun requestNavDrawerClose() {
        _closeNavDrawerSignal.value = true
    }

    fun closeNavDrawerCompleted() {
        _closeNavDrawerSignal.value = false
    }

    private fun disableDrawer() {
        _enableDrawer.value = false
    }

    private fun enableDrawer() {
        _enableDrawer.value = true
    }
}
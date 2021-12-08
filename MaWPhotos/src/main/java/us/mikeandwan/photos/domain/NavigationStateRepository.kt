package us.mikeandwan.photos.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import us.mikeandwan.photos.R
import us.mikeandwan.photos.domain.models.NavigationArea
import us.mikeandwan.photos.domain.models.NavigationInstruction
import us.mikeandwan.photos.domain.models.PhotoCategory
import javax.inject.Inject

class NavigationStateRepository @Inject constructor(
    private val activeIdRepository: ActiveIdRepository,
    private val photoCategoryRepository: PhotoCategoryRepository
) {
    private val mutex = Mutex()

    private val _closeNavDrawerSignal = MutableStateFlow(false)
    val closeNavDrawerSignal = _closeNavDrawerSignal.asStateFlow()

    private val _openNavDrawerSignal = MutableStateFlow(false)
    val openNavDrawerSignal = _openNavDrawerSignal.asStateFlow()

    private val _navigateBackSignal = MutableStateFlow(false)
    val navigateBackSignal = _navigateBackSignal.asStateFlow()

    private val _toolbarTitle = MutableStateFlow("")
    val toolbarTitle = _toolbarTitle.asStateFlow()

    private val _enableDrawer = MutableStateFlow(true)
    val enableDrawer = _enableDrawer.asStateFlow()

    private val _activeDestinationId = MutableStateFlow(-1)

    private val _requestedNavigation = MutableStateFlow(NavigationInstruction(null, null, NavigationArea.None))
    val requestedNavigation = _requestedNavigation.asStateFlow()

    private val _navArea = MutableStateFlow(NavigationArea.None)
    val navArea = _navArea.asStateFlow()

    private val _isPhotoScreen = MutableStateFlow(false)
    val isPhotoScreen = _isPhotoScreen.asStateFlow()

    suspend fun onDestinationChanged(destinationId: Int) {
        mutex.withLock {
            when(destinationId) {
                R.id.navigation_about -> { setDestinationInfo(NavigationArea.About, false, "About") }
                R.id.navigation_categories -> {
                    val year = activeIdRepository.getActivePhotoCategoryYear().first()?.toString() ?: ""

                    setDestinationInfo(NavigationArea.Category, true, year)
                }
                R.id.navigation_search -> { setDestinationInfo(NavigationArea.Search, true, "Search") }
                R.id.navigation_settings -> { setDestinationInfo(NavigationArea.Settings, false, "Settings") }
                R.id.navigation_random -> { setDestinationInfo(NavigationArea.Random, true, "Random") }
                R.id.navigation_category -> { setDestinationInfo(NavigationArea.Category, true, photoCategoryRepository.getCategory().first().name) }
                R.id.navigation_photo -> { _isPhotoScreen.value = true }
                R.id.navigation_upload -> { setDestinationInfo(NavigationArea.Upload, false, "Upload Queue") }
            }

            requestNavDrawerClose()

            _activeDestinationId.value = destinationId
        }
    }

    fun requestNavDrawerClose() {
        _closeNavDrawerSignal.value = true
    }

    fun requestNavDrawerOpen() {
        _openNavDrawerSignal.value = true
    }

    fun closeNavDrawerCompleted() {
        _closeNavDrawerSignal.value = false
    }

    fun openNavDrawerCompleted() {
        _openNavDrawerSignal.value = false
    }

    fun requestNavigateBack() {
        _navigateBackSignal.value = true
    }

    fun navigateBackCompleted() {
        _navigateBackSignal.value = false
    }

    suspend fun requestNavigateToYear(year: Int) {
        activeIdRepository.clearActiveCategory()
        activeIdRepository.setActivePhotoCategoryYear(year)

        requestNavigation(NavigationArea.Category, R.id.action_navigate_to_categories)
    }

    suspend fun requestNavigateToCategory(category: PhotoCategory) {
        activeIdRepository.clearActivePhoto()
        activeIdRepository.setActivePhotoCategoryYear(category.year)
        activeIdRepository.setActivePhotoCategory(category.id)

        requestNavigation(NavigationArea.Category, R.id.navigation_category)
    }

    fun requestNavigateToArea(area: NavigationArea) {
        when(area) {
            NavigationArea.Category -> requestNavigation(area, R.id.action_navigate_to_categories)
            NavigationArea.Random -> requestNavigation(area, R.id.action_navigate_to_random)
            NavigationArea.Upload -> requestNavigation(area, R.id.action_navigate_to_upload)
            NavigationArea.About -> requestNavigation(area, R.id.action_navigate_to_about)
            NavigationArea.Search -> requestNavigation(area, R.id.action_navigate_to_search)
            NavigationArea.Settings -> requestNavigation(area, R.id.action_navigate_to_settings)
            NavigationArea.Login -> requestNavigation(area, null)
            else -> throw Exception("cannot navigate to area: $area")
        }
    }

    fun requestNavigationCompleted() {
        _requestedNavigation.value = NavigationInstruction(null, null, NavigationArea.None)
    }

    fun overrideTitle(title: String) {
        _toolbarTitle.value = title
    }

    fun setIsOnLoginScreen() {
        _navArea.value = NavigationArea.Login
        requestNavigationCompleted()
    }

    private fun requestNavigation(destinationArea: NavigationArea, id: Int?) {
        var popBackId: Int? = null

        if(isPhotoScreen.value && _activeDestinationId.value == R.id.navigation_photo) {
            popBackId = when (navArea.value) {
                NavigationArea.Category -> R.id.navigation_category
                NavigationArea.Random -> R.id.navigation_random
                else -> null
            }
        }

        _requestedNavigation.value = NavigationInstruction(id, popBackId, destinationArea)
    }

    private fun disableDrawer() {
        _enableDrawer.value = false
    }

    private fun enableDrawer() {
        _enableDrawer.value = true
    }

    private fun setDestinationInfo(navArea: NavigationArea, enableDrawer: Boolean, title: String) {
        CoroutineScope(Dispatchers.IO).launch {
            activeIdRepository.clearActivePhoto()
        }

        _isPhotoScreen.value = false
        _navArea.value = navArea
        _toolbarTitle.value = title

        if(enableDrawer) enableDrawer() else disableDrawer()
    }
}
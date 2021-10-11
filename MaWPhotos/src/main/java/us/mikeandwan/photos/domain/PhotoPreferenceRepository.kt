package us.mikeandwan.photos.domain

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import us.mikeandwan.photos.database.PhotoPreferenceDao
import javax.inject.Inject

class PhotoPreferenceRepository @Inject constructor (
    private val dao: PhotoPreferenceDao
) {
    fun getPhotoPreferences() = dao
        .getPhotoPreference(Constants.ID)
        .map { it.toDomainPhotoPreference() }

    fun getDisplayToolbar() = dao
        .getPhotoPreference(Constants.ID)
        .map { it.displayToolbar }

    fun getDisplayThumbnails() = dao
        .getPhotoPreference(Constants.ID)
        .map { it.displayThumbnails }

    fun getDisplayTopToolbar() = dao
        .getPhotoPreference(Constants.ID)
        .map { it.displayTopToolbar }

    fun getDoFadeControls() = dao
        .getPhotoPreference(Constants.ID)
        .map { it.doFadeControls }

    fun getSlideshowIntervalSeconds() = dao
        .getPhotoPreference(Constants.ID)
        .map { it.slideshowIntervalSeconds }

    suspend fun setDisplayToolbar(doDisplayToolbar: Boolean) {
        setPreference { it.copy(displayToolbar = doDisplayToolbar) }
    }

    suspend fun setDisplayThumbnails(doDisplayThumbnails: Boolean) {
        setPreference { it.copy(displayThumbnails = doDisplayThumbnails) }
    }

    suspend fun setDisplayTopToolbar(doDisplayTopToolbar: Boolean) {
        setPreference { it.copy(displayTopToolbar = doDisplayTopToolbar) }
    }

    suspend fun setDoFadeControls(doFadeControls: Boolean) {
        setPreference { it.copy(doFadeControls = doFadeControls) }
    }

    suspend fun setSlideshowIntervalSeconds(seconds: Int) {
        setPreference { it.copy(slideshowIntervalSeconds = seconds) }
    }

    private suspend fun setPhotoPreferences(pref: PhotoPreference) {
        val dbPref = us.mikeandwan.photos.database.PhotoPreference(
            Constants.ID,
            pref.displayToolbar,
            pref.displayThumbnails,
            pref.displayTopToolbar,
            pref.doFadeControls,
            pref.slideshowIntervalSeconds)

        dao.setPhotoPreference(dbPref)
    }

    private suspend fun setPreference(update: (pref: PhotoPreference) -> PhotoPreference) {
        val pref = getPhotoPreferences().first()

        setPhotoPreferences(update(pref))
    }

    object Constants {
        const val ID = 1
    }
}
package us.mikeandwan.photos.domain

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import us.mikeandwan.photos.database.PhotoPreferenceDao
import us.mikeandwan.photos.domain.models.GridThumbnailSize
import us.mikeandwan.photos.domain.models.PhotoPreference
import javax.inject.Inject

class PhotoPreferenceRepository @Inject constructor (
    private val dao: PhotoPreferenceDao
) {
    fun getSlideshowIntervalSeconds() = dao
        .getPhotoPreference(Constants.ID)
        .map { it.slideshowIntervalSeconds }

    fun getPhotoGridItemSize() = dao
        .getPhotoPreference(Constants.ID)
        .map { it.gridThumbnailSize}

    suspend fun setSlideshowIntervalSeconds(seconds: Int) {
        setPreference { it.copy(slideshowIntervalSeconds = seconds) }
    }

    suspend fun setPhotoGridItemSize(size: GridThumbnailSize) {
        setPreference { it.copy(gridThumbnailSize = size) }
    }

    private fun getPhotoPreferences() = dao
        .getPhotoPreference(Constants.ID)
        .map { it.toDomainPhotoPreference() }

    private suspend fun setPhotoPreferences(pref: PhotoPreference) {
        val dbPref = us.mikeandwan.photos.database.PhotoPreference(
            Constants.ID,
            pref.slideshowIntervalSeconds,
            pref.gridThumbnailSize)

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
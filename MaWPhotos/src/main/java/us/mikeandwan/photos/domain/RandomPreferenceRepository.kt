package us.mikeandwan.photos.domain

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import us.mikeandwan.photos.database.RandomPreferenceDao
import us.mikeandwan.photos.domain.models.GridThumbnailSize
import us.mikeandwan.photos.domain.models.RandomPreference
import javax.inject.Inject

class RandomPreferenceRepository @Inject constructor (
    private val dao: RandomPreferenceDao
) {
    fun getRandomPreferences() = dao
        .getRandomPreference(Constants.ID)
        .map { it.toDomainRandomPreference() }

    fun getSlideshowIntervalSeconds() = dao
        .getRandomPreference(Constants.ID)
        .map { it.slideshowIntervalSeconds }

    fun getPhotoGridItemSize() = dao
        .getRandomPreference(Constants.ID)
        .map { it.gridThumbnailSize}

    suspend fun setSlideshowIntervalSeconds(seconds: Int) {
        setPreference { it.copy(slideshowIntervalSeconds = seconds) }
    }

    suspend fun setPhotoGridItemSize(size: GridThumbnailSize) {
        setPreference { it.copy(gridThumbnailSize = size) }
    }

    private suspend fun setRandomPreferences(pref: RandomPreference) {
        val dbPref = us.mikeandwan.photos.database.RandomPreference(
            Constants.ID,
            pref.slideshowIntervalSeconds,
            pref.gridThumbnailSize)

        dao.setRandomPreference(dbPref)
    }

    private suspend fun setPreference(update: (pref: RandomPreference) -> RandomPreference) {
        val pref = getRandomPreferences().first()

        setRandomPreferences(update(pref))
    }

    object Constants {
        const val ID = 1
    }
}
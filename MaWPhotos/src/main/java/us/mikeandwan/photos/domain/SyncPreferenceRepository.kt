package us.mikeandwan.photos.domain

import kotlinx.coroutines.flow.map
import us.mikeandwan.photos.database.SyncPreferenceDao
import javax.inject.Inject

class SyncPreferenceRepository @Inject constructor (
    private val dao: SyncPreferenceDao
) {
    fun getSyncPreferences() = dao
        .getSyncPreference(Constants.ID)
        .map { it.toDomainSyncPreference() }

    fun getSyncFrequencyHours() = dao
        .getSyncPreference(Constants.ID)
        .map { it.syncFrequencyHours }

    suspend fun setSyncPreference(pref: SyncPreference) {
        val dbPref = us.mikeandwan.photos.database.SyncPreference(
            Constants.ID,
            pref.syncFrequencyHours
        )

        dao.setSyncPreference(dbPref)
    }

    object Constants {
        const val ID = 1
    }
}
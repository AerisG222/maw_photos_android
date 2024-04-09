package us.mikeandwan.photos.domain

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import us.mikeandwan.photos.database.NotificationPreferenceDao
import us.mikeandwan.photos.domain.models.NotificationPreference
import javax.inject.Inject

class NotificationPreferenceRepository @Inject constructor(
    private val dao: NotificationPreferenceDao
) {
    fun getDoNotify() = dao
        .getNotificationPreference(Constants.ID)
        .map { it.doNotify }

    fun getDoVibrate() = dao
        .getNotificationPreference(Constants.ID)
        .map { it.doVibrate }

    suspend fun setDoNotify(doNotify: Boolean) {
        setPreference { it.copy(doNotify = doNotify) }
    }

    suspend fun setDoVibrate(doVibrate: Boolean) {
        setPreference { it.copy(doVibrate = doVibrate) }
    }

    private fun getNotificationPreferences() = dao
        .getNotificationPreference(Constants.ID)
        .map { it.toDomainNotificationPreference() }

    private suspend fun setNotificationPreferences(pref: NotificationPreference) {
        val dbPref = us.mikeandwan.photos.database.NotificationPreference(Constants.ID, pref.doNotify, pref.doVibrate)

        dao.setNotificationPreference(dbPref)
    }

    private suspend fun setPreference(update: (pref: NotificationPreference) -> NotificationPreference) {
        val pref = getNotificationPreferences().first()

        setNotificationPreferences(update(pref))
    }

    object Constants {
        const val ID = 1
    }
}
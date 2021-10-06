package us.mikeandwan.photos.domain

import kotlinx.coroutines.flow.map
import us.mikeandwan.photos.database.NotificationPreferenceDao
import javax.inject.Inject

class NotificationPreferenceRepository @Inject constructor(
    private val dao: NotificationPreferenceDao
) {
    fun getNotificationPreferences() = dao
        .getNotificationPreference(Constants.ID)
        .map { it.toDomainNotificationPreference() }

    fun getDoNotify() = dao
        .getNotificationPreference(Constants.ID)
        .map { it.doNotify }

    fun getDoVibrate() = dao
        .getNotificationPreference(Constants.ID)
        .map { it.doVibrate }

    suspend fun setNotificationPreferences(pref: NotificationPreference) {
        val dbPref = us.mikeandwan.photos.database.NotificationPreference(Constants.ID, pref.doNotify, pref.doVibrate)

        dao.setNotificationPreference(dbPref)
    }

    object Constants {
        const val ID = 1
    }
}
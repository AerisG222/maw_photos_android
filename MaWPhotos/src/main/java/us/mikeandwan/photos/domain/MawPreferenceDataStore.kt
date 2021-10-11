package us.mikeandwan.photos.domain

import androidx.preference.PreferenceDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MawPreferenceDataStore(
    private val categoryPreferenceRepository: CategoryPreferenceRepository,
    private val notificationPreferenceRepository: NotificationPreferenceRepository,
    private val photoPreferenceRepository: PhotoPreferenceRepository
) : PreferenceDataStore() {
    override fun getBoolean(key: String?, defValue: Boolean): Boolean {
        return runBlocking {
            when (key) {
                "notifications_new_message" -> notificationPreferenceRepository.getDoNotify().first()
                "notifications_new_message_vibrate" -> notificationPreferenceRepository.getDoVibrate().first()
                "display_top_toolbar" -> photoPreferenceRepository.getDisplayTopToolbar().first()
                "display_toolbar" -> photoPreferenceRepository.getDisplayToolbar().first()
                "display_thumbnails" -> photoPreferenceRepository.getDisplayThumbnails().first()
                "fade_controls" -> photoPreferenceRepository.getDoFadeControls().first()
                else -> throw IllegalArgumentException("Invalid boolean key: $key")
            }
        }
    }

    override fun getString(key: String?, defValue: String?): String? {
        return runBlocking {
            when(key) {
                "category_view_mode" -> categoryPreferenceRepository.getCategoryPreference().first().displayType.toString()
                "slideshow_interval" -> photoPreferenceRepository.getSlideshowIntervalSeconds().first().toString()
                else -> throw IllegalArgumentException("Invalid string key: $key")
            }
        }
    }

    override fun putBoolean(key: String?, value: Boolean) {
        runBlocking {
            when (key) {
                "notifications_new_message" -> notificationPreferenceRepository.setDoNotify(value)
                "notifications_new_message_vibrate" -> notificationPreferenceRepository.setDoVibrate(value)
                "display_top_toolbar" -> photoPreferenceRepository.setDisplayTopToolbar(value)
                "display_toolbar" -> photoPreferenceRepository.setDisplayToolbar(value)
                "display_thumbnails" -> photoPreferenceRepository.setDisplayThumbnails(value)
                "fade_controls" -> photoPreferenceRepository.setDoFadeControls(value)
                else -> throw IllegalArgumentException("Invalid boolean key: $key")
            }
        }
    }

    override fun putString(key: String?, value: String?) {
        if(value == null) {
            return
        }

        runBlocking {
            when(key) {
                "category_view_mode" -> categoryPreferenceRepository.setCategoryDisplayType(enumValueOf(value))
                "slideshow_interval" -> photoPreferenceRepository.setSlideshowIntervalSeconds(value.toInt())
                else -> throw IllegalArgumentException("Invalid string key: $key")
            }
        }
    }
}
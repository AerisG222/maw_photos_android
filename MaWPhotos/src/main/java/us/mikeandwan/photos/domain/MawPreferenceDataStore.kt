package us.mikeandwan.photos.domain

import androidx.preference.PreferenceDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MawPreferenceDataStore(
    private val categoryPreferenceRepository: CategoryPreferenceRepository,
    private val notificationPreferenceRepository: NotificationPreferenceRepository,
    private val photoPreferenceRepository: PhotoPreferenceRepository,
    private val randomPreferenceRepository: RandomPreferenceRepository,
    private val searchPreferenceRepository: SearchPreferenceRepository
) : PreferenceDataStore() {
    override fun getBoolean(key: String?, defValue: Boolean): Boolean {
        return runBlocking {
            when (key) {
                "notifications_new_message" -> notificationPreferenceRepository.getDoNotify().first()
                "notifications_new_message_vibrate" -> notificationPreferenceRepository.getDoVibrate().first()
                else -> throw IllegalArgumentException("Invalid boolean key: $key")
            }
        }
    }

    override fun getString(key: String?, defValue: String?): String {
        return runBlocking {
            when(key) {
                "category_grid_thumbnail_size" -> categoryPreferenceRepository.getCategoryGridItemSize().first().toString()
                "category_view_mode" -> categoryPreferenceRepository.getCategoryDisplayType().first().toString()
                "photo_grid_thumbnail_size" -> photoPreferenceRepository.getPhotoGridItemSize().first().toString()
                "photo_slideshow_interval" -> photoPreferenceRepository.getSlideshowIntervalSeconds().first().toString()
                "random_grid_thumbnail_size" -> randomPreferenceRepository.getPhotoGridItemSize().first().toString()
                "random_slideshow_interval" -> randomPreferenceRepository.getSlideshowIntervalSeconds().first().toString()
                "search_save_query_count" -> searchPreferenceRepository.getSearchesToSaveCount().first().toString()
                "search_grid_thumbnail_size" -> searchPreferenceRepository.getSearchGridItemSize().first().toString()
                "search_view_mode" -> searchPreferenceRepository.getSearchDisplayType().first().toString()
                else -> throw IllegalArgumentException("Invalid string key: $key")
            }
        }
    }

    override fun putBoolean(key: String?, value: Boolean) {
        runBlocking {
            when (key) {
                "notifications_new_message" -> notificationPreferenceRepository.setDoNotify(value)
                "notifications_new_message_vibrate" -> notificationPreferenceRepository.setDoVibrate(value)
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
                "category_grid_thumbnail_size" -> categoryPreferenceRepository.setCategoryGridItemSize(enumValueOf(value))
                "category_view_mode" -> categoryPreferenceRepository.setCategoryDisplayType(enumValueOf(value))
                "photo_grid_thumbnail_size" -> photoPreferenceRepository.setPhotoGridItemSize(enumValueOf(value))
                "photo_slideshow_interval" -> photoPreferenceRepository.setSlideshowIntervalSeconds(value.toInt())
                "random_grid_thumbnail_size" -> randomPreferenceRepository.setPhotoGridItemSize(enumValueOf(value))
                "random_slideshow_interval" -> randomPreferenceRepository.setSlideshowIntervalSeconds(value.toInt())
                "search_save_query_count" -> searchPreferenceRepository.setSearchesToSaveCount(value.toInt())
                "search_grid_thumbnail_size" -> searchPreferenceRepository.setSearchGridItemSize(enumValueOf(value))
                "search_view_mode" -> searchPreferenceRepository.setSearchDisplayType(enumValueOf(value))
                else -> throw IllegalArgumentException("Invalid string key: $key")
            }
        }
    }
}
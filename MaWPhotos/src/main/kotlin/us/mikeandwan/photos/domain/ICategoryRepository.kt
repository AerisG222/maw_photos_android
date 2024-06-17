package us.mikeandwan.photos.domain

import kotlinx.coroutines.flow.Flow
import us.mikeandwan.photos.domain.models.ExternalCallStatus
import us.mikeandwan.photos.domain.models.MediaCategory

interface ICategoryRepository {
    fun getYears(): Flow<List<Int>>
    fun getMostRecentYear(): Flow<Int?>
    fun getNewCategories(): Flow<ExternalCallStatus<List<MediaCategory>>>
    fun getCategories(year: Int): Flow<List<MediaCategory>>
}

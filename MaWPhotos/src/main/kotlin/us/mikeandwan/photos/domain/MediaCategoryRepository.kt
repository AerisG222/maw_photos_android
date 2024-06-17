package us.mikeandwan.photos.domain

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import us.mikeandwan.photos.database.MediaCategoryDao
import us.mikeandwan.photos.domain.models.ExternalCallStatus
import us.mikeandwan.photos.domain.models.MediaCategory
import javax.inject.Inject

class MediaCategoryRepository @Inject constructor(
    private val mcDao: MediaCategoryDao,
    private val pcRepo: PhotoCategoryRepository,
    private val vcRepo: VideoCategoryRepository
) : ICategoryRepository {
    override fun getYears() = flow {
        val data = mcDao.getYears()

        if(data.first().isEmpty()) {
            emit(emptyList())
            getNewCategories()
        }

        emitAll(data)
    }

    override fun getMostRecentYear() = mcDao.getMostRecentYear()

    override fun getNewCategories() = flow {
        var err = true

        emit(ExternalCallStatus.Loading)

        val photoCategories = pcRepo.getNewCategories()
        val videoCategories = vcRepo.getNewCategories()
        val cat = mutableListOf<MediaCategory>()

        photoCategories.combine(videoCategories) { pc, vc ->
            if (pc is ExternalCallStatus.Success) {
                cat += pc.result
                err = false
            }
            if (vc is ExternalCallStatus.Success) {
                cat += vc.result
                err = false
            }

            if(!err) {
                emit(ExternalCallStatus.Success(cat.toList()))
            }
        }.collect()
    }

    override fun getCategories(year: Int) = mcDao
        .getCategoriesForYear(year)
        .map { dbList ->
            dbList.map { dbCat -> dbCat.toDomainMediaCategory() }
        }
}

package us.mikeandwan.photos.domain

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
                .collect { }
        }

        emitAll(data)
    }

    override fun getMostRecentYear() = mcDao.getMostRecentYear()

    override fun getNewCategories() = flow {
        emit(ExternalCallStatus.Loading)

        val photoCategoriesResult = try {
            pcRepo.getNewCategories().first { it !is ExternalCallStatus.Loading }
        } catch (e: NoSuchElementException) {
            ExternalCallStatus.Error("No photo categories emitted.")
        }

        val videoCategoriesResult = try {
            vcRepo.getNewCategories().first { it !is ExternalCallStatus.Loading }
        } catch (e: NoSuchElementException) {
            ExternalCallStatus.Error("No video categories emitted.")
        }

        val combinedCategories = mutableListOf<MediaCategory>()

        if (photoCategoriesResult is ExternalCallStatus.Success) {
            combinedCategories.addAll(photoCategoriesResult.result)
        }

        if (videoCategoriesResult is ExternalCallStatus.Success) {
            combinedCategories.addAll(videoCategoriesResult.result)
        }

        when {
            photoCategoriesResult is ExternalCallStatus.Error ||
            videoCategoriesResult is ExternalCallStatus.Error ->
                emit(ExternalCallStatus.Error("Unable to load all categories."))
            else ->
                emit(ExternalCallStatus.Success(combinedCategories))
        }
    }

    override fun getCategories(year: Int) = mcDao
        .getCategoriesForYear(year)
        .map { dbList ->
            dbList.map { dbCat -> dbCat.toDomainMediaCategory() }
        }
}

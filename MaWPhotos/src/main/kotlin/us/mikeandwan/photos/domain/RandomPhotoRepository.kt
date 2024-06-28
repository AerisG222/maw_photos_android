package us.mikeandwan.photos.domain

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import us.mikeandwan.photos.api.ApiResult
import us.mikeandwan.photos.api.PhotoApiClient
import us.mikeandwan.photos.domain.models.ExternalCallStatus
import us.mikeandwan.photos.domain.models.Photo
import us.mikeandwan.photos.domain.models.RandomPreference
import javax.inject.Inject

class RandomPhotoRepository @Inject constructor(
    private val api: PhotoApiClient,
    randomPreferenceRepository: RandomPreferenceRepository,
    private val apiErrorHandler: ApiErrorHandler
) {
    companion object {
        const val ERR_MSG_FETCH = "Unable to fetch random photos at this time.  Please try again later."
    }

    private var scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var periodicJob: PeriodicJob<ExternalCallStatus<List<Photo>>>

    private val slideshowDurationInMillis = randomPreferenceRepository
        .getSlideshowIntervalSeconds()
        .map { it * 1000L }
        .stateIn(scope, WhileSubscribed(5000), RandomPreference().slideshowIntervalSeconds * 1000L)

    private val _photos = MutableStateFlow(emptyList<Photo>())
    val photos = _photos.asStateFlow()

    fun setDoFetch(doFetch: Boolean) {
        if(doFetch) {
            periodicJob.start()
        } else {
            periodicJob.stop()
        }
    }

    fun fetch(count: Int) = flow {
        emit(ExternalCallStatus.Loading)

        when(val result = api.getRandomPhotos(count)) {
            is ApiResult.Error -> emit(apiErrorHandler.handleError(result, ERR_MSG_FETCH))
            is ApiResult.Empty -> emit(apiErrorHandler.handleEmpty(result, ERR_MSG_FETCH))
            is ApiResult.Success -> {
                val newPhotos = result.result.items.map { it.toDomainPhoto() }

                _photos.value += newPhotos

                emit(ExternalCallStatus.Success(newPhotos))
            }
        }
    }

    fun clear() {
        _photos.value = emptyList()
    }

    init {
        periodicJob = PeriodicJob(
            false,
            slideshowDurationInMillis.value
        ) { fetch(1) }
    }
}

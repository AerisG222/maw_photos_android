package us.mikeandwan.photos.domain

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import us.mikeandwan.photos.api.ApiResult
import us.mikeandwan.photos.api.PhotoApiClient
import us.mikeandwan.photos.domain.models.ExternalCallStatus
import us.mikeandwan.photos.domain.models.Photo
import us.mikeandwan.photos.domain.models.RANDOM_PREFERENCE_DEFAULT
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
    private var fetchNextJob: Job? = null

    private val _doFetch = MutableStateFlow(false)
    private val doFetch = _doFetch.asStateFlow()

    private val slideshowDurationInMillis = randomPreferenceRepository
        .getRandomPreferences()
        .map { prefs -> (prefs.slideshowIntervalSeconds * 1000).toLong() }
        .stateIn(scope, SharingStarted.Eagerly, (RANDOM_PREFERENCE_DEFAULT.slideshowIntervalSeconds * 1000).toLong())

    private val _photos = MutableStateFlow(emptyList<Photo>())
    val photos = _photos.asStateFlow()

    fun setDoFetch(doFetch: Boolean) {
        _doFetch.value = doFetch
    }

    fun fetch(count: Int) = flow {
        emit(ExternalCallStatus.Loading)

        when(val result = api.getRandomPhotos(count)) {
            is ApiResult.Error -> emit(apiErrorHandler.handleError(result, ERR_MSG_FETCH))
            is ApiResult.Empty -> emit(apiErrorHandler.handleEmpty(result, ERR_MSG_FETCH))
            is ApiResult.Success -> {
                var newPhotos = emptyList<Photo>()

                if(result.result.items.isNotEmpty()) {
                    newPhotos = result.result.items.map { it.toDomainPhoto() }

                    _photos.value += newPhotos
                }

                emit(ExternalCallStatus.Success(newPhotos))
            }
        }
    }

    fun clear() {
        _photos.value = emptyList()
    }

    private fun scheduleFetchNext(scope: CoroutineScope, interval: Long) {
        scope.launch {
            fetchNextJob = launch {
                while(true) {
                    delay(interval)
                    fetch(1).collect { }
                }
            }
        }
    }

    private fun cancelFetchNext() {
        fetchNextJob?.cancel("Stopping fetch", null)
        fetchNextJob = null
    }

    init {
        scope.launch {
            combine(
                doFetch,
                slideshowDurationInMillis
            ){ doFetch, interval -> Pair(doFetch, interval) }
            .onEach { (doFetch, interval) ->
                if(doFetch) {
                    if(fetchNextJob != null) {
                        cancelFetchNext()
                    }

                    scheduleFetchNext(this, interval)
                } else {
                    cancelFetchNext()
                }
            }
            .launchIn(this)
        }
    }
}
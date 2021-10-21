package us.mikeandwan.photos.domain

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import us.mikeandwan.photos.api.PhotoApiClient
import javax.inject.Inject

class RandomPhotoRepository @Inject constructor(
    private val api: PhotoApiClient,
    private val randomPreferenceRepository: RandomPreferenceRepository
) {
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

    suspend fun fetch(count: Int) {
        val result = api.getRandomPhotos(count)

        if(result == null || result.items.isEmpty()) {
            return
        }

        val newPhotos = result.items.map { it.toDomainPhoto() }

        val x = _photos.value + newPhotos

        _photos.value = x
    }

    fun clear() {
        _photos.value = emptyList()
    }

    private fun scheduleFetchNext(scope: CoroutineScope, interval: Long) {
        scope.launch {
            fetchNextJob = launch {
                while(true) {
                    delay(interval)
                    fetch(1)
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
            doFetch
                .combine(slideshowDurationInMillis){ doFetch, interval -> Pair(doFetch, interval)}
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
package us.mikeandwan.photos.api

import android.webkit.MimeTypeMap
import kotlinx.coroutines.delay
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import timber.log.Timber
import java.io.File

abstract class BaseApiClient {
    companion object {
        private const val MAX_ATTEMPTS = 3
    }

    private val mimeMap by lazy { MimeTypeMap.getSingleton() }

    protected suspend fun <T> makeApiCall(name: String, apiCall: suspend () -> retrofit2.Response<T>): ApiResult<T> {
        var attempt = 1
        var exception: Throwable? = null

        while (attempt <= MAX_ATTEMPTS) {
            exception = null

            if(attempt > 1) {
                delay(200L * attempt)
            }

            try {
                val response = apiCall()
                val result = ApiResult.build(response)

                if(result is ApiResult.Error) {
                    Timber.w("$name failed: ${result.error} (attempt: $attempt)")

                    attempt++
                    continue
                } else {
                    when (result) {
                        is ApiResult.Success -> Timber.d("$name succeeded")
                        is ApiResult.Empty -> Timber.d("$name was empty")
                        is ApiResult.Error -> Timber.w("should not happen!")
                    }

                    return result
                }
            } catch (t: Throwable) {
                exception = t
                Timber.e("$name failed: ${t.message} (attempt: $attempt)", t)

                attempt++
            }
        }

        return ApiResult.Error("$name failed", null, exception)
    }

    protected fun getMediaTypeForFile(file: File): MediaType {
        return mimeMap
            .getMimeTypeFromExtension(file.extension)
                ?.toMediaTypeOrNull() ?: "binary/octet-stream".toMediaType()
    }
}

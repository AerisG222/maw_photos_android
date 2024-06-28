package us.mikeandwan.photos.api

import android.webkit.MimeTypeMap
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import timber.log.Timber
import java.io.File

abstract class BaseApiClient {
    private val mimeMap by lazy { MimeTypeMap.getSingleton() }

    protected suspend fun <T> makeApiCall(name: String, apiCall: suspend () -> retrofit2.Response<T>): ApiResult<T> {
        Timber.d("$name starting")

        try {
            val response = apiCall()
            val result = ApiResult.build(response)

            when(result) {
                is ApiResult.Error -> Timber.w("$name failed: ${result.error}")
                is ApiResult.Success -> Timber.d("$name succeeded")
                is ApiResult.Empty -> Timber.d("$name was empty")
            }

            return result
        } catch (t: Throwable) {
            Timber.e(t)

            return ApiResult.Error("$name failed: ${t.message}", null, t)
        }
    }

    protected fun getMediaTypeForFile(file: File): MediaType {
        return mimeMap
            .getMimeTypeFromExtension(file.extension)
                ?.toMediaTypeOrNull() ?: "binary/octet-stream".toMediaType()
    }
}

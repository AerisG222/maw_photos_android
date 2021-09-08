package us.mikeandwan.photos.models

import us.mikeandwan.photos.models.MultimediaAsset
import okhttp3.ResponseBody
import us.mikeandwan.photos.models.FileLocation
import com.fasterxml.jackson.annotation.JsonFormat
import retrofit2.Response
import us.mikeandwan.photos.models.UploadedFile
import java.io.IOException
import java.util.*

class ApiResult<T>(response: Response<T>?) {
    var result: T? = null
    lateinit var error: String
    var isSuccess = false

    init {
        if (response == null) {
            isSuccess = false
            error = "Response was null.  Unable to extract result from API call."
        } else {
            if (response.isSuccessful) {
                isSuccess = true
                result = response.body()
            } else {
                isSuccess = false
                val body = response.errorBody()
                var message = response.message()
                if (body != null) {
                    message = try {
                        body.string()
                    } catch (ioe: IOException) {
                        response.message()
                    }
                }
                error = String.format(
                    Locale.ENGLISH,
                    "api error response: %d | %s",
                    response.code(),
                    message
                )
            }
        }
    }
}
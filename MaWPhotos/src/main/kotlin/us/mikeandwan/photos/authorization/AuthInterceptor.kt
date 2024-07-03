package us.mikeandwan.photos.authorization

import okhttp3.Interceptor
import okhttp3.Response
import us.mikeandwan.photos.domain.AuthorizationRepository
import java.io.IOException

class AuthInterceptor(
    private val authorizationRepository: AuthorizationRepository
) : Interceptor {
    @Synchronized
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val srcRequest = chain.request()
        val accessToken = authorizationRepository.authState.value.accessToken

        if(accessToken != null) {
            val request = srcRequest.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()

            return chain.proceed(request)
        }

        return chain.proceed(srcRequest)
    }
}

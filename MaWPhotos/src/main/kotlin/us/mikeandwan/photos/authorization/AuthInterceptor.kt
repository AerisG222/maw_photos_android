package us.mikeandwan.photos.authorization

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AuthInterceptor(
    private val authStateManager: AuthStateManager
) : Interceptor {
    @Synchronized
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val srcRequest = chain.request()
        val accessToken = authStateManager.current.accessToken

        if(accessToken != null) {
            val request = srcRequest.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()

            return chain.proceed(request)
        }

        return chain.proceed(srcRequest)
    }
}

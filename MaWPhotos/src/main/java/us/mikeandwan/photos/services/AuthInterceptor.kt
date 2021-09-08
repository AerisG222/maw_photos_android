package us.mikeandwan.photos.services

import us.mikeandwan.photos.services.AuthStateManager
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import kotlin.jvm.Synchronized
import kotlin.Throws

class AuthInterceptor(private val _authStateManager: AuthStateManager) : Interceptor {
    @Synchronized
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val srcRequest = chain.request()
        val request = srcRequest.newBuilder()
            .addHeader(
                "Authorization",
                String.format("Bearer %s", _authStateManager.current.accessToken)
            )
            .build()
        return chain.proceed(request)
    }
}
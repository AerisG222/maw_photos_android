package us.mikeandwan.photos.services;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


public class AuthInterceptor implements Interceptor {
    private AuthStateManager _authStateManager;

    public AuthInterceptor(AuthStateManager authStateManager) {
        _authStateManager = authStateManager;
    }


    @Override
    public synchronized Response intercept(Interceptor.Chain chain) throws IOException {
        Request srcRequest = chain.request();

        Request request = srcRequest.newBuilder()
            .addHeader("Authorization", String.format("Bearer %s", _authStateManager.getCurrent().getAccessToken()))
            .build();

        return chain.proceed(request);
    }
}

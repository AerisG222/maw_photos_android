package us.mikeandwan.photos.services;

import net.openid.appauth.AuthState;

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
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request srcRequest = chain.request();

        AuthState authState = _authStateManager.getCurrent();

        if(authState != null) {
            Request request = srcRequest.newBuilder()
                .addHeader("Authorization", String.format("Bearer %s", authState.getAccessToken()))
                .build();

            return chain.proceed(request);
        }

        return chain.proceed(srcRequest);
    }
}

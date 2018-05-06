package us.mikeandwan.photos.services;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationService;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


public class AuthInterceptor implements Interceptor {
    private AuthStateManager _authStateManager;
    private AuthorizationService _authService;


    public AuthInterceptor(AuthorizationService authService, AuthStateManager authStateManager) {
        _authStateManager = authStateManager;
        _authService = authService;
    }


    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request srcRequest = chain.request();
        String[] accessTokenHolder = new String[1];

        AuthState authState = _authStateManager.getCurrent();

        authState.performActionWithFreshTokens(_authService, (accessToken, idToken, ex) -> {
            accessTokenHolder[0] = accessToken;
        });

        Request request = srcRequest.newBuilder()
            .addHeader("Authorization", String.format("Bearer %s", accessTokenHolder[0]))
            .build();

        return chain.proceed(request);
    }
}

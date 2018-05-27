package us.mikeandwan.photos.services;

import android.util.Log;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationService;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import us.mikeandwan.photos.MawApplication;


public class AuthInterceptor implements Interceptor {
    private AuthStateManager _authStateManager;
    private AuthorizationService _authService;

    private String _accessToken;


    public AuthInterceptor(AuthorizationService authService, AuthStateManager authStateManager) {
        _authStateManager = authStateManager;
        _authService = authService;
    }


    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request srcRequest = chain.request();

        Request request = srcRequest.newBuilder()
            .addHeader("Authorization", String.format("Bearer %s", getAccessToken()))
            .build();

        return chain.proceed(request);
    }


    // https://www.programcreek.com/java-api-examples/?code=approov/AppAuth-OAuth2-Books-Demo/AppAuth-OAuth2-Books-Demo-master/app/src/main/java/com/criticalblue/auth/demo/auth/AuthRepo.java
    private String getAccessToken() {
        CountDownLatch actionComplete = new CountDownLatch(1);
        AuthState authState = _authStateManager.getCurrent();

        if(!authState.isAuthorized()) {
            Log.e(MawApplication.LOG_TAG, "NOT AUTHORIZED!");

            return null;
        }

        authState.performActionWithFreshTokens(_authService, (String authToken,
                                                              String idToken,
                                                              AuthorizationException ex) -> {
            if(ex != null) {
                Log.e(MawApplication.LOG_TAG, "Error getting fresh access token: " + ex.getMessage());
            } else {
                Log.d(MawApplication.LOG_TAG, "Got updated access token: " + authToken);
                Log.d(MawApplication.LOG_TAG, "access tokens are equal: " + authToken.equals(_authStateManager.getCurrent().getAccessToken()));
            }

            _accessToken = authToken;
            actionComplete.countDown();
        });

        boolean complete;

        try {
            complete = actionComplete.await(5000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            complete = false;
        }

        if (!complete) {
            _accessToken = null;
        }

        String token = _accessToken;

        _accessToken = null;

        return token;
    }
}

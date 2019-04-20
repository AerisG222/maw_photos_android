package us.mikeandwan.photos.services;

import android.util.Log;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationService;

import java.io.IOException;

import java9.util.concurrent.CompletableFuture;
import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import us.mikeandwan.photos.MawApplication;


public class AuthAuthenticator implements Authenticator {
    private AuthorizationService _authService;
    private AuthStateManager _authStateManager;


    public AuthAuthenticator(AuthorizationService authService, AuthStateManager authStateManager) {
        _authService = authService;
        _authStateManager = authStateManager;
    }


    @Override
    public synchronized Request authenticate(Route route, Response response) throws IOException {
        CompletableFuture<Request> future = new CompletableFuture<>();
        AuthState authState = _authStateManager.getCurrent();

        Log.d(MawApplication.LOG_TAG, "Starting Authenticator.authenticate");

        authState.performActionWithFreshTokens(_authService, (String accessToken,
                                                              String idToken,
                                                              AuthorizationException ex) -> {
            if (ex != null) {
                Log.e(MawApplication.LOG_TAG, String.format("Failed to authorize = %s", ex.getMessage()));
                future.complete(null);
            }
            else if (accessToken == null) {
                Log.e(MawApplication.LOG_TAG, "Failed to authorize, received null access token");
                future.complete(null); // Give up, we've already failed to authenticate.
            } else {
                Log.i(MawApplication.LOG_TAG, "authenticate: obtained access token");

                Request request = response.request().newBuilder()
                        .header("Authorization", String.format("Bearer %s", accessToken))
                        .build();

                future.complete(request);
            }
        });

        try {
            return future.get();
        } catch(Exception ex) {
            Log.e(MawApplication.LOG_TAG, String.format("Error: %s", ex.getMessage()));

            return null;
        }
    }
}

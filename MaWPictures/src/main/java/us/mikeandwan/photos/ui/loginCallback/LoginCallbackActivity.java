package us.mikeandwan.photos.ui.loginCallback;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;

import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ClientAuthentication;
import net.openid.appauth.TokenRequest;
import net.openid.appauth.TokenResponse;

import javax.inject.Inject;

import butterknife.ButterKnife;
import io.reactivex.Observable;
import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.di.ActivityComponent;
import us.mikeandwan.photos.di.DaggerActivityComponent;
import us.mikeandwan.photos.services.AuthStateManager;
import us.mikeandwan.photos.ui.BaseActivity;
import us.mikeandwan.photos.ui.HasComponent;
import us.mikeandwan.photos.ui.initialLoad.InitialLoadActivity;


public class LoginCallbackActivity extends BaseActivity implements HasComponent<ActivityComponent> {
    @Inject AuthStateManager _authStateManager;
    @Inject Observable<AuthorizationServiceConfiguration> _config;
    @Inject AuthorizationService _authService;

    private ActivityComponent _activityComponent;

    public ActivityComponent getComponent() {
        return _activityComponent;
    }


    // https://github.com/openid/AppAuth-Android/blob/master/app/java/net/openid/appauthdemo/TokenActivity.java
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_callback);
        ButterKnife.bind(this);

        _activityComponent = DaggerActivityComponent.builder()
            .applicationComponent(getApplicationComponent())
            .activityModule(getActivityModule())
            .build();

        _activityComponent.inject(this);

        AuthorizationResponse response = AuthorizationResponse.fromIntent(getIntent());
        AuthorizationException ex = AuthorizationException.fromIntent(getIntent());

        if (response != null || ex != null) {
            _authStateManager.updateAfterAuthorization(response, ex);
        }

        if (response != null && response.authorizationCode != null) {
            // authorization code exchange is required
            _authStateManager.updateAfterAuthorization(response, ex);
            exchangeAuthorizationCode(response);
        } else if (ex != null) {
            Log.e(MawApplication.LOG_TAG, "error authenticating: " + ex.getMessage());
            //displayNotAuthorized("Authorization flow failed: " + ex.getMessage());
        } else {
            Log.e(MawApplication.LOG_TAG, "No authorization state retained - reauthorization required");
            //displayNotAuthorized("No authorization state retained - reauthorization required");
        }
    }


    @MainThread
    private void exchangeAuthorizationCode(AuthorizationResponse authorizationResponse) {
        Log.d(MawApplication.LOG_TAG, "a");
        //displayLoading("Exchanging authorization code");
        performTokenRequest(
            authorizationResponse.createTokenExchangeRequest(),
            this::handleCodeExchangeResponse);
    }


    @MainThread
    private void performTokenRequest(TokenRequest request, AuthorizationService.TokenResponseCallback callback) {
        Log.d(MawApplication.LOG_TAG, "b");
        ClientAuthentication clientAuthentication;

        try {
            clientAuthentication = _authStateManager.getCurrent().getClientAuthentication();
        } catch (ClientAuthentication.UnsupportedAuthenticationMethod ex) {
            Log.d(MawApplication.LOG_TAG, "Token request cannot be made, client authentication for the token "
                + "endpoint could not be constructed (%s)", ex);
            //displayNotAuthorized("Client authentication method is unsupported");
            return;
        }

        _authService.performTokenRequest(
            request,
            clientAuthentication,
            callback);
    }


    @WorkerThread
    private void handleCodeExchangeResponse(@Nullable TokenResponse tokenResponse, @Nullable AuthorizationException authException) {
        Log.d(MawApplication.LOG_TAG, "c");

        _authStateManager.updateAfterTokenResponse(tokenResponse, authException);

        if (!_authStateManager.getCurrent().isAuthorized()) {
            final String message = "Authorization Code exchange failed" + ((authException != null) ? authException.error : "");

            // WrongThread inference is incorrect for lambdas
            //noinspection WrongThread
            // runOnUiThread(() -> displayNotAuthorized(message));
            Log.e(MawApplication.LOG_TAG, "NOT AUTHORIZED: " + message);
        } else {
            Log.e(MawApplication.LOG_TAG, "AUTHORIZED");
           // runOnUiThread(this::displayAuthorized);
            goToInitialLoad();
        }
    }


    private void goToInitialLoad() {
        Intent intent = new Intent(this, InitialLoadActivity.class);
        startActivity(intent);

        finish();
    }
}

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
import butterknife.OnClick;
import io.reactivex.Observable;
import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.di.ActivityComponent;
import us.mikeandwan.photos.di.DaggerActivityComponent;
import us.mikeandwan.photos.services.AuthStateManager;
import us.mikeandwan.photos.ui.BaseActivity;
import us.mikeandwan.photos.ui.HasComponent;
import us.mikeandwan.photos.ui.initialLoad.InitialLoadActivity;
import us.mikeandwan.photos.ui.login.LoginActivity;


public class LoginCallbackActivity extends BaseActivity implements HasComponent<ActivityComponent> {
    @Inject AuthStateManager _authStateManager;
    @Inject Observable<AuthorizationServiceConfiguration> _config;
    @Inject AuthorizationService _authService;

    @OnClick(R.id.retryLoginButton) void onLoginButtonClick() {
        retryLogin();
    }

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
    }


    @Override
    public void onStart() {
        super.onStart();

        if(_authStateManager.getCurrent().isAuthorized()) {
            goToInitialLoad();
        }

        AuthorizationResponse response = AuthorizationResponse.fromIntent(getIntent());
        AuthorizationException ex = AuthorizationException.fromIntent(getIntent());

        if (response != null || ex != null) {
            _authStateManager.updateAfterAuthorization(response, ex);
        }

        if (response != null && response.authorizationCode != null) {
            _authStateManager.updateAfterAuthorization(response, ex);
            exchangeAuthorizationCode(response);
        } else if (ex != null) {
            Log.e(MawApplication.LOG_TAG, "Authorization failed: " + ex.getMessage());
        } else {
            Log.e(MawApplication.LOG_TAG, "No authorization state retained - reauthorization required");
        }
    }

    @MainThread
    private void exchangeAuthorizationCode(AuthorizationResponse authorizationResponse) {
        Log.d(MawApplication.LOG_TAG, "Exchanging authorization code");

        performTokenRequest(
            authorizationResponse.createTokenExchangeRequest(),
            this::handleCodeExchangeResponse);
    }


    @MainThread
    private void performTokenRequest(TokenRequest request, AuthorizationService.TokenResponseCallback callback) {
        ClientAuthentication clientAuthentication;

        try {
            Log.d(MawApplication.LOG_TAG, "Attempting token request");
            clientAuthentication = _authStateManager.getCurrent().getClientAuthentication();
        } catch (ClientAuthentication.UnsupportedAuthenticationMethod ex) {
            Log.d(MawApplication.LOG_TAG, "Token request cannot be made, client authentication for the token "
                + "endpoint could not be constructed (%s)", ex);

            return;
        }

        _authService.performTokenRequest(
            request,
            clientAuthentication,
            callback);
    }


    @WorkerThread
    private void handleCodeExchangeResponse(@Nullable TokenResponse tokenResponse, @Nullable AuthorizationException authException) {
        _authStateManager.updateAfterTokenResponse(tokenResponse, authException);

        if (!_authStateManager.getCurrent().isAuthorized()) {
            final String message = "Authorization Code exchange failed" + ((authException != null) ? authException.error : "");

            Log.e(MawApplication.LOG_TAG, "NOT AUTHORIZED: " + message);
        } else {
            Log.d(MawApplication.LOG_TAG, "AUTHORIZED");
            Log.d(MawApplication.LOG_TAG, "auth token: " + _authStateManager.getCurrent().getAccessToken());
            Log.d(MawApplication.LOG_TAG, "refresh token: " + _authStateManager.getCurrent().getRefreshToken());
            Log.d(MawApplication.LOG_TAG, "id token: " + _authStateManager.getCurrent().getIdToken());

            goToInitialLoad();
        }
    }


    private void goToInitialLoad() {
        Intent intent = new Intent(this, InitialLoadActivity.class);
        startActivity(intent);

        finish();
    }


    private void retryLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        finish();
    }
}

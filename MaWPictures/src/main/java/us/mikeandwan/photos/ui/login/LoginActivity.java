package us.mikeandwan.photos.ui.login;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.di.ActivityComponent;
import us.mikeandwan.photos.di.DaggerActivityComponent;
import us.mikeandwan.photos.services.AuthStateManager;
import us.mikeandwan.photos.ui.BaseActivity;
import us.mikeandwan.photos.ui.HasComponent;
import us.mikeandwan.photos.ui.loginCallback.LoginCallbackActivity;
import us.mikeandwan.photos.ui.mode.ModeSelectionActivity;


public class LoginActivity extends BaseActivity implements HasComponent<ActivityComponent> {
    private final CompositeDisposable _disposables = new CompositeDisposable();

    @BindString(R.string.auth_client_id) String _authClientId;
    @BindString(R.string.auth_scheme_redirect_uri) String _authSchemeRedirect;

    @Inject AuthStateManager _authStateManager;
    @Inject Observable<AuthorizationServiceConfiguration> _config;
    AuthorizationService _authService;

    @OnClick(R.id.loginButton) void onLoginButtonClick() {
        retryLogin();
    }

    private Uri _authSchemeRedirectUri;
    private ActivityComponent _activityComponent;


    public ActivityComponent getComponent() {
        return _activityComponent;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        _authSchemeRedirectUri = Uri.parse(_authSchemeRedirect);

        _activityComponent = DaggerActivityComponent.builder()
            .applicationComponent(getApplicationComponent())
            .activityModule(getActivityModule())
            .build();

        _activityComponent.inject(this);

        // https://github.com/openid/AppAuth-Android/issues/333
        _authService = new AuthorizationService(this);

        authorize();
    }


    @Override public void onDestroy() {
        _disposables.clear();

        super.onDestroy();
    }


    private void goToModeSelection() {
        Intent intent = new Intent(this, ModeSelectionActivity.class);
        startActivity(intent);

        finish();
    }


    public void authorize() {
        if(isAuthorized()) {
            // we go to mode selection here, because if a user has previously gained access,
            // if they are off/slow network, then they get stuck for a bit on the blank loading
            // screen which might not be needed - so take them straight in
            goToModeSelection();
            return;
        }

        _disposables.add(_config.subscribe((config) -> {
            _authStateManager.replace(new AuthState(config));

            AuthorizationRequest.Builder authRequestBuilder =
                new AuthorizationRequest.Builder(
                    config,
                    _authClientId, // the client ID, typically pre-registered and static
                    ResponseTypeValues.CODE, // the response_type value: we want a code
                    _authSchemeRedirectUri); // the redirect URI to which the auth response is sent

            AuthorizationRequest authRequest = authRequestBuilder
                .setScopes("openid offline_access profile email role maw_api")
                .build();

            _authService.performAuthorizationRequest(
                authRequest,
                PendingIntent.getActivity(this, 0, new Intent(this, LoginCallbackActivity.class), 0),
                PendingIntent.getActivity(this, 0, new Intent(this, LoginActivity.class), 0));
        }, (ex) -> {
            Log.e(MawApplication.LOG_TAG, "There was an error getting OIDC configuration: " + ex.getMessage());
            handleApiException(ex);
        }));
    }


    private boolean isAuthorized() {
        AuthState authState = _authStateManager.getCurrent();

        return authState.isAuthorized();
    }


    private void retryLogin() {
        recreate();
    }
}

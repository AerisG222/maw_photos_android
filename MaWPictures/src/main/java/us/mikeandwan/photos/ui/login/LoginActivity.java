package us.mikeandwan.photos.ui.login;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.ButterKnife;
import us.mikeandwan.photos.Constants;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.di.ActivityComponent;
import us.mikeandwan.photos.di.DaggerActivityComponent;
import us.mikeandwan.photos.services.AuthStateManager;
import us.mikeandwan.photos.ui.BaseActivity;
import us.mikeandwan.photos.ui.HasComponent;
import us.mikeandwan.photos.ui.initialLoad.InitialLoadActivity;
import us.mikeandwan.photos.ui.loginCallback.LoginCallbackActivity;


public class LoginActivity extends BaseActivity implements HasComponent<ActivityComponent> {
    @BindString(R.string.auth_client_id) String _authClientId;
    @BindString(R.string.auth_scheme_redirect_uri) String _authSchemeRedirect;

    @Inject AuthStateManager _authStateManager;
    @Inject AuthorizationServiceConfiguration _config;
    @Inject AuthorizationService _authService;

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

        authorize();
    }


    private void goToInitialLoad() {
        Intent intent = new Intent(this, InitialLoadActivity.class);
        startActivity(intent);

        finish();
    }


    public void authorize() {
        if(isAuthorized()) {
            goToInitialLoad();
        }

        _authStateManager.replace(new AuthState(_config));

        AuthorizationRequest.Builder authRequestBuilder =
            new AuthorizationRequest.Builder(
                _config,
                _authClientId, // the client ID, typically pre-registered and static
                ResponseTypeValues.CODE, // the response_type value: we want a code
                _authSchemeRedirectUri); // the redirect URI to which the auth response is sent

        AuthorizationRequest authRequest = authRequestBuilder
            .setScopes("openid email role maw_api")
            .build();

        _authService.performAuthorizationRequest(
            authRequest,
            PendingIntent.getActivity(this, 0, new Intent(this, LoginCallbackActivity.class), 0),
            PendingIntent.getActivity(this, 0, new Intent(this, LoginCallbackActivity.class), 0));
    }


    private boolean isAuthorized() {
        AuthState authState = _authStateManager.getCurrent();

        if(authState == null) {
            return false;
        }

        return authState.isAuthorized();
    }
}

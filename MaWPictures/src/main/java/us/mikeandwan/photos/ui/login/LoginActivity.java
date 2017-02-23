package us.mikeandwan.photos.ui.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.di.ActivityComponent;
import us.mikeandwan.photos.di.DaggerActivityComponent;
import us.mikeandwan.photos.models.Credentials;
import us.mikeandwan.photos.services.DataServices;
import us.mikeandwan.photos.ui.BaseActivity;
import us.mikeandwan.photos.ui.HasComponent;
import us.mikeandwan.photos.ui.mode.ModeSelectionActivity;


// TODO: change how we cache credentials for server side encryption
public class LoginActivity extends BaseActivity implements HasComponent<ActivityComponent> {
    private final CompositeDisposable _disposables = new CompositeDisposable();
    private Credentials _creds = new Credentials();
    private ActivityComponent _activityComponent;
    private MawApplication _app;

    @BindView(R.id.username) EditText _usernameView;
    @BindView(R.id.password) EditText _passwordView;
    @BindView(R.id.login_progress) View _progressView;
    @BindView(R.id.login_form) View _loginFormView;
    @BindView(R.id.login_button) Button _loginButton;

    @Inject DataServices _dataServices;


    public ActivityComponent getComponent() {
        return _activityComponent;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        _app = (MawApplication) getApplication();

        _activityComponent = DaggerActivityComponent.builder()
                .applicationComponent(getApplicationComponent())
                .activityModule(getActivityModule())
                .build();

        _activityComponent.inject(this);

        cleanupLegacyStorage();

        ResetNotifications();
        ViewCompat.setElevation(_progressView, 4);

        _creds = _dataServices.getCredentials();

        if (_creds != null) {
            _usernameView.setText(_creds.getUsername());
            _passwordView.setText(_creds.getPassword());

            attemptLogin();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        _disposables.clear(); // do not send event after activity has been destroyed
    }


    private void ResetNotifications() {
        NotificationManager mgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mgr.cancel(0);
        _app.setNotificationCount(0);
    }


    @OnClick(R.id.login_button)
    public void attemptLogin() {
        _loginButton.setEnabled(false);

        _creds.setUsername(_usernameView.getText().toString());
        _creds.setPassword(_passwordView.getText().toString());

        _usernameView.setError(null);
        _passwordView.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(_creds.getPassword())) {
            _passwordView.setError(getString(R.string.act_login_error_field_required));
            focusView = _passwordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(_creds.getUsername())) {
            _usernameView.setError(getString(R.string.act_login_error_field_required));
            focusView = _usernameView;
            cancel = true;
        }

        if (cancel) {
            showProgress(false);
            focusView.requestFocus();
        } else {
            showProgress(true);

            _disposables.add(
                    Flowable.fromCallable(() -> _dataServices.authenticate(_creds))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    this::completeLoginProcess,
                                    ex -> {
                                        showProgress(false);
                                        Log.w(MawApplication.LOG_TAG, "error authenticating: " + ex.getMessage());
                                    }
                            )
            );
        }
    }


    private void completeLoginProcess(boolean success) {
        Log.i(MawApplication.LOG_TAG, "login result: " + success);

        if (success) {
            // set the creds before it is blanked out below
            _dataServices.setCredentials(_creds);
        }

        // always blank out the password after an attempt
        _creds.setPassword("");
        _passwordView.setText("");

        if (success) {
            Snackbar.make(_loginFormView, "Welcome, " + _creds.getUsername(), Snackbar.LENGTH_SHORT).show();

            _disposables.add(
                    Flowable.fromCallable(() -> _dataServices.getRecentCategories())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    x -> goToModeSelection(),
                                    ex -> {
                                        showProgress(false);
                                        Log.w(MawApplication.LOG_TAG, "error loading categories: " + ex.getMessage());
                                    }
                            )
            );
        } else {
            Snackbar.make(_loginFormView, "Unable to authenticate", Snackbar.LENGTH_SHORT).show();
            _passwordView.requestFocus();
            showProgress(false);
        }
    }


    private void goToModeSelection() {
        Intent intent = new Intent(this, ModeSelectionActivity.class);
        startActivity(intent);

        showProgress(false);
    }


    private void cleanupLegacyStorage() {
        Log.i(MawApplication.LOG_TAG, "starting to wipe");

        _disposables.add(
                Flowable.fromCallable(() -> {
                            _dataServices.wipeLegacyCache();
                            return true;
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                x -> Log.i(MawApplication.LOG_TAG, "completed wipe"),
                                ex -> Log.w(MawApplication.LOG_TAG, "error wiping: " + ex.getMessage())
                        )
        );
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        _loginButton.setEnabled(!show);

        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        _progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        _progressView.animate()
                .setDuration(shortAnimTime)
                .alpha(show ? 1 : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        _progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                    }
                });
    }
}

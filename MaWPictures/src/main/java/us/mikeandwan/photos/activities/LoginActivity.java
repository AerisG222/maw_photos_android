package us.mikeandwan.photos.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.EditorAction;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.data.Category;
import us.mikeandwan.photos.data.Credentials;
import us.mikeandwan.photos.data.MawDataManager;
import us.mikeandwan.photos.services.PhotoStorage;
import us.mikeandwan.photos.tasks.BackgroundTask;
import us.mikeandwan.photos.tasks.BackgroundTaskExecutor;
import us.mikeandwan.photos.tasks.BackgroundTaskPriority;
import us.mikeandwan.photos.tasks.GetCategoriesForYearBackgroundTask;
import us.mikeandwan.photos.tasks.GetYearsBackgroundTask;
import us.mikeandwan.photos.tasks.LoginBackgroundTask;


@SuppressWarnings("ALL")
@SuppressLint("Registered")
@EActivity(R.layout.activity_login)
public class LoginActivity extends AppCompatActivity {
    private Credentials _creds = new Credentials();

    @SystemService
    NotificationManager _notificationManager;

    @ViewById(R.id.username)
    protected EditText _usernameView;

    @ViewById(R.id.password)
    protected EditText _passwordView;

    @ViewById(R.id.login_progress)
    protected View _progressView;

    @ViewById(R.id.login_form)
    protected View _loginFormView;

    @ViewById(R.id.login_button)
    protected Button _loginButton;

    @App
    MawApplication _app;

    @Bean
    MawDataManager _dm;

    @Bean
    PhotoStorage _ps;


    @AfterInject
    protected void afterInject() {
        cleanupLegacyStorage();
    }


    private void cleanupLegacyStorage() {
        Log.i(MawApplication.LOG_TAG, "starting to wipe");

        _ps.wipeLegacyCache();
    }


    @AfterViews
    protected void afterViews() {
        ResetNotifications();
        ViewCompat.setElevation(_progressView, 20);

        _creds = _dm.getCredentials();

        if (_creds != null) {
            _usernameView.setText(_creds.getUsername());
            _passwordView.setText(_creds.getPassword());

            attemptLogin();
        }
    }


    private void ResetNotifications() {
        _notificationManager.cancel(0);
        MawApplication.setNotificationCount(0);
    }


    @EditorAction(R.id.password)
    public boolean onPaswordEditorAction(TextView textView, int id, KeyEvent keyEvent) {
        if (id == R.id.login || id == EditorInfo.IME_NULL) {
            attemptLogin();
            return true;
        }
        return false;
    }


    @Click(R.id.login_button)
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

            BackgroundTask task = new LoginBackgroundTask(getBaseContext(), _creds) {
                @Override
                protected void postExecuteTask(Boolean result) {
                    completeLoginProcess(result);
                }
            };

            BackgroundTaskExecutor.getInstance().enqueueTask(task);
        }
    }


    private void completeLoginProcess(boolean success) {
        if (success) {
            // set the creds before it is blanked out below
            _dm.setCredentials(_creds.getUsername(), _creds.getPassword());
        }

        // always blank out the password after an attempt
        _creds.setPassword("");
        _passwordView.setText("");

        if (success) {
            Snackbar.make(_loginFormView, "Welcome, " + _creds.getUsername(), Snackbar.LENGTH_SHORT).show();

            // if this is the first time a user is accessing the system, prepare the initial list of categories now
            if (_dm.getPhotoYears().size() == 0) {
                BackgroundTask task = new GetYearsBackgroundTask(getBaseContext()) {
                    @Override
                    protected void postExecuteTask(List<Integer> result) {
                        getCategories(result);
                    }
                };

                BackgroundTaskExecutor.getInstance().enqueueTask(task);
            } else {
                goToModeSelection();
            }
        } else {
            Snackbar.make(_loginFormView, "Unable to authenticate", Snackbar.LENGTH_SHORT).show();
            _passwordView.requestFocus();
            showProgress(false);
        }
    }


    private void getCategories(List<Integer> years) {
        BackgroundTaskExecutor executor = BackgroundTaskExecutor.getInstance();

        // the first one will get the categories for the most recent year, and this will control if
        // we move to the next screen
        BackgroundTask task = new GetCategoriesForYearBackgroundTask(getBaseContext(), years.get(0)) {
            @Override
            protected void postExecuteTask(List<Category> result) {
                goToModeSelection();
            }
        };

        // before we enqueue, ensure we set this to highest priority, to force this to be evaluated first
        task.setPriority(BackgroundTaskPriority.VeryHigh);

        executor.enqueueTask(task);

        // now for the remaining years, just schedule these in the background, but they should not
        // notify the main ui in any way, as this is driven by the above call
        for (int i = 1; i < years.size(); i++) {
            executor.enqueueTask(new GetCategoriesForYearBackgroundTask(getBaseContext(), years.get(i)));
        }
    }


    private void goToModeSelection() {
        Intent intent = new Intent(this, ModeSelectionActivity_.class);
        startActivity(intent);

        showProgress(false);
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        _loginButton.setEnabled(!show);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            _progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            _progressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    _progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            _progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}

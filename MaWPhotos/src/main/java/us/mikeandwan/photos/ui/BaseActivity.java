package us.mikeandwan.photos.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.view.ViewCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;

import net.openid.appauth.AuthorizationException;

import java.net.ConnectException;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.PublishSubject;
import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.di.ActivityModule;
import us.mikeandwan.photos.di.ApplicationComponent;


@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {
    private final CompositeDisposable _disposables = new CompositeDisposable();
    private final PublishSubject<String> _errorSubject = PublishSubject.create();

    public void onApiException(Throwable throwable) {
        handleApiException(throwable);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getApplicationComponent().inject(this);

        _disposables.add(_errorSubject
            .publish(publishedItems -> publishedItems
                .take(1)
                .concatWith(publishedItems
                    .skip(1)
                    .debounce(2, TimeUnit.SECONDS)
                )
            )
            .subscribe(this::showError));
    }


    @Override
    protected void onDestroy() {
        _disposables.clear();

        super.onDestroy();
    }


    protected ApplicationComponent getApplicationComponent() {
        return ((MawApplication)getApplication()).getApplicationComponent();
    }


    protected ActivityModule getActivityModule() {
        return new ActivityModule(this);
    }


    protected void updateToolbar(Toolbar toolbar, String title) {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ViewCompat.setElevation(toolbar, 4);

            if(title != null) {
                toolbar.setTitle(title);
            }
        }
    }


    protected void handleApiException(Throwable throwable) {
        if(throwable == null) {
            return;
        }

        Log.e(MawApplication.LOG_TAG, "Error accessing api: " + throwable.getMessage());

        if(throwable instanceof ConnectException) {
            _errorSubject.onNext("Unable to connect to service at this time.");
        }
        else if(throwable instanceof AuthorizationException) {
            _errorSubject.onNext("Authorization failed.");
        }
    }


    private void showError(String msg) {
        View view = findViewById(android.R.id.content);

        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show();
    }
}

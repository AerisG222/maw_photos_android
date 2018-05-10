package us.mikeandwan.photos.ui.initialLoad;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.di.ActivityComponent;
import us.mikeandwan.photos.di.DaggerActivityComponent;
import us.mikeandwan.photos.services.DataServices;
import us.mikeandwan.photos.ui.BaseActivity;
import us.mikeandwan.photos.ui.HasComponent;
import us.mikeandwan.photos.ui.mode.ModeSelectionActivity;


public class InitialLoadActivity extends BaseActivity implements HasComponent<ActivityComponent> {
    private final CompositeDisposable _disposables = new CompositeDisposable();
    private ActivityComponent _activityComponent;

    @BindView(R.id.initialLoadLayout) ConstraintLayout _layout;

    @Inject DataServices _dataServices;


    public ActivityComponent getComponent() {
        return _activityComponent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_load);

        ButterKnife.bind(this);

        _activityComponent = DaggerActivityComponent.builder()
            .applicationComponent(getApplicationComponent())
            .activityModule(getActivityModule())
            .build();

        _activityComponent.inject(this);

        completeLoginProcess();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        _disposables.clear(); // do not send event after activity has been destroyed
    }


    private void completeLoginProcess() {
        Snackbar.make(_layout, "Getting things ready...", Snackbar.LENGTH_SHORT).show();

        _disposables.add(
            Flowable.fromCallable(() -> _dataServices.getRecentCategories())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    x -> goToModeSelection(),
                    ex -> {
                        Log.e(MawApplication.LOG_TAG, "error loading categories: " + ex.getMessage());
                        handleApiException(ex);
                        goToModeSelection();
                    }
                )
        );
    }


    private void goToModeSelection() {
        Intent intent = new Intent(this, ModeSelectionActivity.class);
        startActivity(intent);

        finish();
    }


    /* TODO: delete? / re-enable?
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
    */
}

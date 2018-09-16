package us.mikeandwan.photos.ui.receiver;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.ViewTreeObserver;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.Observable;
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


public class PhotoReceiverActivity extends BaseActivity implements HasComponent<ActivityComponent> {
    private final CompositeDisposable _disposables = new CompositeDisposable();
    private ActivityComponent _activityComponent;

    @Inject DataServices _dataServices;
    @Inject ReceiverRecyclerAdapter _receiverAdapter;

    @BindDimen(R.dimen.category_grid_thumbnail_size) int _thumbSize;
    @BindView(R.id.receiver_recycler_view) RecyclerView _recyclerView;
    @BindView(R.id.photoReceiverLayout) ConstraintLayout _layout;
    @BindView(R.id.toolbar) Toolbar _toolbar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_receiver);

        ButterKnife.bind(this);

        _activityComponent = DaggerActivityComponent.builder()
            .applicationComponent(getApplicationComponent())
            .activityModule(getActivityModule())
            .build();

        _activityComponent.inject(this);

        _recyclerView.setHasFixedSize(true);
        _recyclerView.setAdapter(_receiverAdapter);
        setLayoutManager();

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if(isValidType(type))
        {
            switch(action) {
                case Intent.ACTION_SEND:
                    handleSendSingle(intent);
                    break;
                case Intent.ACTION_SEND_MULTIPLE:
                    handleSendMultiple(intent);
                    break;
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //MenuInflater inflater = getMenuInflater();

        //inflater.inflate(R.menu.category_list, menu);

        return true;
    }


    @Override
    public void onResume() {
        updateToolbar(_toolbar, "Upload Queue");

        _disposables.add(Observable
            .interval(1000, 10000, TimeUnit.MILLISECONDS)
            .map(x -> _dataServices.getFilesQueuedForUpload())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                this::updateListing
            ));

        super.onResume();
    }


    @Override
    protected void onDestroy() {
        _disposables.clear(); // do not send event after activity has been destroyed

        super.onDestroy();
    }


    public ActivityComponent getComponent() {
        return _activityComponent;
    }


    private void setLayoutManager() {
        // https://stackoverflow.com/questions/33575731/gridlayoutmanager-how-to-auto-fit-columns
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int cols = displayMetrics.widthPixels / _thumbSize;

        GridLayoutManager glm = new GridLayoutManager(this, Math.max(1, cols));
        _recyclerView.setLayoutManager(glm);
        _recyclerView.setItemAnimator(new DefaultItemAnimator());

        _receiverAdapter.setItemSize(displayMetrics.widthPixels / cols);

        _recyclerView.getRecycledViewPool().clear();
    }


    private boolean isValidType(String type) {
        return type != null && (type.startsWith("image/") || type.startsWith("video/"));
    }


    void handleSendSingle(Intent intent) {
        Uri mediaUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);

        if (mediaUri != null) {
            ArrayList<Uri> list = new ArrayList<>();

            list.add(mediaUri);

            saveFiles(list);
        }
    }


    void handleSendMultiple(Intent intent) {
        ArrayList<Uri> mediaUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);

        if (mediaUris != null) {
            saveFiles(mediaUris);
        }
    }


    void saveFiles(ArrayList<Uri> mediaUris) {
        _disposables.add(
            Flowable.fromCallable(() -> {
                int count = 0;

                for(Uri uri : mediaUris) {
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    String type = getContentResolver().getType(uri);

                    if(_dataServices.enequeFileToUpload(inputStream, type))
                    {
                        count++;
                    }
                }

                File[] files = _dataServices.getFilesQueuedForUpload();

                return new FilesQueuedResult(count, files);
            })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    result -> {
                        if(result.getCount() == 0) {
                            Snackbar.make(_layout, "Unable to add items to the upload queue =(", Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(_layout, "Added " + result.getCount() + " item(s) to the upload queue.", Snackbar.LENGTH_SHORT).show();
                            updateListing(result.getQueuedFiles());
                        }
                    },
                    ex -> {
                        Log.e(MawApplication.LOG_TAG, "error loading categories: " + ex.getMessage());
                        handleApiException(ex);
                    }
                ));
    }


    private void updateListing(File[] files) {
        _receiverAdapter.setQueuedFiles(files);
    }


    private void goHome() {
        Intent intent = new Intent(this, ModeSelectionActivity.class);
        startActivity(intent);

        finish();
    }
}

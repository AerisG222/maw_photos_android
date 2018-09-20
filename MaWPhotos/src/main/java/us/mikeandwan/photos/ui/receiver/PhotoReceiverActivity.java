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
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindDimen;
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
import us.mikeandwan.photos.services.UploadJobScheduler;
import us.mikeandwan.photos.ui.BaseActivity;
import us.mikeandwan.photos.ui.HasComponent;


public class PhotoReceiverActivity extends BaseActivity implements HasComponent<ActivityComponent> {
    private final CompositeDisposable _disposables = new CompositeDisposable();
    private ActivityComponent _activityComponent;

    @Inject DataServices _dataServices;
    @Inject ReceiverRecyclerAdapter _receiverAdapter;
    @Inject UploadJobScheduler _uploadScheduler;

    @BindDimen(R.dimen.category_grid_thumbnail_size) int _thumbSize;
    @BindView(R.id.receiver_recycler_view) RecyclerView _recyclerView;
    @BindView(R.id.receiver_wifi_text_view) TextView _wifiTextView;
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

        if(action != null)
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

        // if we end up on this page, we either have new files to upload, or a user wants to check
        // so lets try to reschedule the job to kick it off
        _uploadScheduler.schedule(true);
    }


    @Override
    public void onResume() {
        updateToolbar(_toolbar, "Upload Queue");

        _disposables.add(_dataServices
            .getFileQueueObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                this::updateListing
            ));

        super.onResume();
    }


    @Override
    protected void onDestroy() {
        _disposables.clear();

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
        ArrayList<Uri> list = new ArrayList<>();

        list.add(mediaUri);

        saveFiles(list);
    }


    void handleSendMultiple(Intent intent) {
        ArrayList<Uri> mediaUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);

        saveFiles(mediaUris);
    }


    void saveFiles(ArrayList<Uri> mediaUris) {
        _disposables.add(
            Flowable.fromCallable(() -> enqueueFiles(mediaUris))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    msg -> Snackbar.make(_layout, msg, Snackbar.LENGTH_LONG).show(),
                    ex -> {
                        Log.e(MawApplication.LOG_TAG, "error loading categories: " + ex.getMessage());
                        handleApiException(ex);
                    }
                ));
    }


    private String enqueueFiles(ArrayList<Uri> mediaUris) throws FileNotFoundException {
        int count = 0;
        int unsupportedFiles = 0;

        for(Uri uri : mediaUris) {
            String type = getContentResolver().getType(uri);

            if(!isValidType(type)) {
                unsupportedFiles++;
                continue;
            }

            InputStream inputStream = getContentResolver().openInputStream(uri);

            if(_dataServices.enequeFileToUpload(count + 1, inputStream, type))
            {
                count++;
            }
        }

        String msg = "";

        if(count > 0) {
            msg += "Successfully enqueued " + count + " files for upload.";
        }

        if(unsupportedFiles > 0) {
            if(msg.length() > 0) {
                msg += "  ";
            }

            msg += "Unable to enqueue " + unsupportedFiles + " files.";
        }

        return msg;
    }


    private void updateListing(File[] files) {
        _receiverAdapter.setQueuedFiles(files);
    }
}

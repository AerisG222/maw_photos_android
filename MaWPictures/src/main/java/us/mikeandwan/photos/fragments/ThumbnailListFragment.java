package us.mikeandwan.photos.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.activities.IPhotoActivity;
import us.mikeandwan.photos.activities.LoginActivity;
import us.mikeandwan.photos.activities.PhotoListActivity;
import us.mikeandwan.photos.di.TaskComponent;
import us.mikeandwan.photos.models.Photo;
import us.mikeandwan.photos.models.PhotoSize;
import us.mikeandwan.photos.services.MawAuthenticationException;
import us.mikeandwan.photos.services.PhotoStorage;
import us.mikeandwan.photos.tasks.DownloadPhotoTask;


public class ThumbnailListFragment extends BasePhotoFragment {
    private final CompositeDisposable disposables = new CompositeDisposable();
    private int _thumbIndex;
    private final List<ImageView> _thumbList = new ArrayList<>();
    private Unbinder _unbinder;

    @BindView(R.id.horizontalScrollView) HorizontalScrollView _horizontalScrollView;
    @BindView(R.id.imageRecycler) RecyclerView _imageRecyclerView;

    @Inject PhotoStorage _photoStorage;
    @Inject
    DownloadPhotoTask _downloadPhotoTask;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_thumbnail_list, container, false);
        _unbinder = ButterKnife.bind(this, view);

        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.getComponent(TaskComponent.class).inject(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        disposables.clear(); // do not send event after activity has been destroyed
        _unbinder.unbind();
    }


    public HorizontalScrollView getThumbnailScrollView() {
        return _horizontalScrollView;
    }


    public void onCurrentPhotoUpdated() {
        // if the change came outside of this fragment, then make sure we move to the current
        // thumbnail.  otherwise, don't change the view as that is unexpected to user
        if (_thumbIndex != getPhotoActivity().getCurrentIndex()) {
            ImageView thumb = _thumbList.get(getPhotoActivity().getCurrentIndex());

            _horizontalScrollView.smoothScrollTo(thumb.getLeft(), 0);

            // TODO: consider adding generic version to a function to the photolistactivity
            // we force the animation here to leave the alpha at 0.2, otherwise was resetting to 1.0
            AlphaAnimation alpha = new AlphaAnimation(PhotoListActivity.FADE_END_ALPHA, PhotoListActivity.FADE_END_ALPHA);
            alpha.setDuration(PhotoListActivity.FADE_DURATION);
            alpha.setFillAfter(true);

            _horizontalScrollView.startAnimation(alpha);
        }
    }


    public void addPhotoList(List<Photo> photoList) {
        synchronized (_thumbList) {
            for (Photo photo : photoList) {
                addPhoto(photo);
            }
        }
    }


    public void addPhoto(Photo photo)
    {
        ImageView imageView = createThumbnail();
        int index = 0;

        synchronized (_thumbList) {
            _thumbList.add(imageView);
            index = _thumbList.size() - 1;
        }

        displayPhotoThumbnail(photo, index);
    }


    private void displayPhotoThumbnail(Photo photo, int index) {
        if (!_photoStorage.doesExist(photo.getXsInfo().getPath())) {
                downloadImage(photo, PhotoSize.Xs, index);
        } else {
            String file = "file://" + _photoStorage.getCachePath(photo.getXsInfo().getPath());
            ImageView thumb = _thumbList.get(index);

            Picasso
                .with(getActivity())
                .load(file)
                .resizeDimen(R.dimen.photo_list_thumbnail_size, R.dimen.photo_list_thumbnail_size)
                .centerCrop()
                .into(thumb);
        }

        updateProgress();
    }


    private void handleException(Throwable ex) {
        if (ex.getCause() instanceof MawAuthenticationException) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    }


    private void downloadImage(final Photo photo, PhotoSize size, int index) {
        disposables.add(Flowable.fromCallable(() -> _downloadPhotoTask.call(photo, size))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        x -> {
                            displayPhotoThumbnail(photo, index);
                            updateProgress();
                        },
                        ex -> handleException(ex)
                )
        );

        updateProgress();
    }


    private ImageView createThumbnail() {
        ImageView image = new ImageView(getActivity());

        try {
            image.setScaleType(ImageView.ScaleType.CENTER);
            image.setPadding(2, 0, 2, 0);
            image.setImageBitmap(_photoStorage.getPlaceholderThumbnail());

            final IPhotoActivity activity = getPhotoActivity();

            image.setOnClickListener(view -> {
                    //noinspection SuspiciousMethodCalls
                    _thumbIndex = _thumbList.indexOf(view);
                    activity.gotoPhoto(_thumbIndex);
            });

            _imageRecyclerView.addView(image);
        } catch (Exception ex) {
            Log.e(MawApplication.LOG_TAG, ex.getMessage());
        }

        return image;
    }
}

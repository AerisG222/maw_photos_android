package us.mikeandwan.photos.fragments;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.activities.IPhotoActivity;
import us.mikeandwan.photos.activities.LoginActivity_;
import us.mikeandwan.photos.activities.PhotoListActivity;
import us.mikeandwan.photos.data.Photo;
import us.mikeandwan.photos.data.PhotoDownload;
import us.mikeandwan.photos.data.PhotoSize;
import us.mikeandwan.photos.services.MawAuthenticationException;
import us.mikeandwan.photos.services.PhotoStorage;
import us.mikeandwan.photos.tasks.BackgroundTaskExecutor;
import us.mikeandwan.photos.tasks.BackgroundTaskPriority;
import us.mikeandwan.photos.tasks.DownloadImageBackgroundTask;


@SuppressWarnings("ALL")
@EFragment(R.layout.fragment_thumbnail_list)
public class ThumbnailListFragment extends BasePhotoFragment {
    private int _thumbIndex;
    private final List<ImageView> _thumbList = new ArrayList<>();
    private PhotoStorage _photoStorage;

    @ViewById(R.id.horizontalScrollView)
    protected HorizontalScrollView _horizontalScrollView;

    @ViewById(R.id.imageLayout)
    protected LinearLayout _imageLayout;

    @App
    MawApplication _app;


    @AfterInject
    protected void afterInject() {
        _photoStorage = new PhotoStorage(_app);
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

        PhotoDownload pd = new PhotoDownload(photo, index);

        displayPhotoThumbnail(pd);
    }


    private void displayPhotoThumbnail(PhotoDownload photoDownload) {
        if (!_photoStorage.doesExist(photoDownload.getMawPhoto().getXsInfo().getPath())) {
            if (photoDownload.getDownloadAttempts() == 0) {
                photoDownload.incrementDownloadCount();
                downloadImage(photoDownload, PhotoSize.Xs, BackgroundTaskPriority.Normal);
            } else {
                Log.w(MawApplication.LOG_TAG, "we have already tried to download this thumbnail w/o success, not trying again");
            }
        } else {
            String file = "file://" + _photoStorage.getCachePath(photoDownload.getMawPhoto().getXsInfo().getPath());
            ImageView thumb = _thumbList.get(photoDownload.getIndex());

            Picasso
                .with(getActivity())
                .load(file)
                .resizeDimen(R.dimen.photo_list_thumbnail_size, R.dimen.photo_list_thumbnail_size)
                .centerCrop()
                .into(thumb);
        }

        updateProgress();
    }


    private void downloadImage(final PhotoDownload photoDownload, PhotoSize size, BackgroundTaskPriority priority) {
        DownloadImageBackgroundTask task = new DownloadImageBackgroundTask(getContext(), photoDownload, size, priority) {
            @Override
            protected void postExecuteTask(PhotoDownload result) {
                displayPhotoThumbnail(result);
                updateProgress();
            }

            @Override
            protected void handleException(ExecutionException ex) {
                if (ex.getCause() instanceof MawAuthenticationException) {
                    startActivity(new Intent(getContext(), LoginActivity_.class));
                }
            }
        };

        BackgroundTaskExecutor.getInstance().enqueueTask(task);

        updateProgress();
    }


    private ImageView createThumbnail() {
        ImageView image = new ImageView(getContext());

        try {
            image.setScaleType(ImageView.ScaleType.CENTER);
            image.setPadding(2, 0, 2, 0);
            image.setImageBitmap(_photoStorage.getPlaceholderThumbnail());

            final IPhotoActivity activity = getPhotoActivity();

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //noinspection SuspiciousMethodCalls
                    _thumbIndex = _thumbList.indexOf(view);
                    activity.gotoPhoto(_thumbIndex);
                }
            });

            _imageLayout.addView(image);
        } catch (Exception ex) {
            Log.e(MawApplication.LOG_TAG, ex.getMessage());
        }

        return image;
    }
}

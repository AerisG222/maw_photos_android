package us.mikeandwan.photos.models.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import io.reactivex.android.schedulers.AndroidSchedulers;
import uk.co.senab.photoview.PhotoView;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import us.mikeandwan.photos.activities.IPhotoActivity;
import us.mikeandwan.photos.activities.LoginActivity;
import us.mikeandwan.photos.models.Photo;
import us.mikeandwan.photos.models.PhotoSize;
import us.mikeandwan.photos.services.MawAuthenticationException;
import us.mikeandwan.photos.services.PhotoApiClient;
import us.mikeandwan.photos.services.PhotoStorage;
import us.mikeandwan.photos.tasks.DownloadPhotoTask;


// http://stackoverflow.com/questions/11306037/how-to-implement-zoom-pan-and-drag-on-viewpager-in-android
public class FullScreenImageAdapter extends PagerAdapter {
    private final Context _context;
    private final IPhotoActivity _activity;
    private final List<Photo> _photoList;
    private final PhotoStorage _photoStorage;
    private final DownloadPhotoTask _downloadPhotoTask;


    public FullScreenImageAdapter(Context context, PhotoStorage photoStorage, PhotoApiClient photoClient, IPhotoActivity activity) {
        _context = context;
        _activity = activity;
        _photoList = activity.getPhotoList();
        _photoStorage = photoStorage;
        _downloadPhotoTask = new DownloadPhotoTask(photoClient);
    }


    @Override
    public int getCount() {
        return _photoList.size();
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PhotoView photoView = new PhotoView(container.getContext());
        photoView.setTag(position)
        ;
        displayImage(photoView, _photoList.get(position));

        container.addView(photoView);

        return photoView;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((PhotoView) object);
    }


    private void displayImage(PhotoView view, Photo photo) {
        String path = photo.getMdInfo().getPath();

        if (!_photoStorage.doesExist(path)) {
            downloadImage(view, photo, PhotoSize.Md);
        } else {
            BitmapDrawable drawable = new BitmapDrawable(_context.getResources(), _photoStorage.get(path));
            view.setImageDrawable(drawable);
        }
    }


    private void handleException(Throwable ex) {
        if (ex.getCause() instanceof MawAuthenticationException) {
            _context.startActivity(new Intent(_context, LoginActivity.class));
        }
    }


    private void downloadImage(final PhotoView view, final Photo photo, PhotoSize size) {
        Flowable.fromCallable(() -> _downloadPhotoTask.call(photo, size))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        x -> {
                            displayImage(view, photo);
                            _activity.updateProgress();
                        },
                        ex -> handleException(ex)
                );

        _activity.updateProgress();
    }
}
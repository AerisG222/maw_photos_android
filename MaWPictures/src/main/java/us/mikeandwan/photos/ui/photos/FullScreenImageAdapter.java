package us.mikeandwan.photos.ui.photos;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import uk.co.senab.photoview.PhotoView;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import us.mikeandwan.photos.models.Photo;
import us.mikeandwan.photos.models.PhotoSize;
import us.mikeandwan.photos.services.AuthenticationExceptionHandler;
import us.mikeandwan.photos.services.DataServices;
import us.mikeandwan.photos.services.PhotoStorage;


// http://stackoverflow.com/questions/11306037/how-to-implement-zoom-pan-and-drag-on-viewpager-in-android
public class FullScreenImageAdapter extends PagerAdapter {
    private final CompositeDisposable _disposables = new CompositeDisposable();
    private final Context _context;
    private final IPhotoActivity _activity;
    private final List<Photo> _photoList;
    private final PhotoStorage _photoStorage;
    private final AuthenticationExceptionHandler _authHandler;
    private final DataServices _dataServices;


    public FullScreenImageAdapter(IPhotoActivity activity, DataServices dataServices, PhotoStorage photoStorage, AuthenticationExceptionHandler authHandler) {
        _context = (Context)activity;
        _activity = activity;
        _photoList = activity.getPhotoList();
        _photoStorage = photoStorage;
        _dataServices = dataServices;
        _authHandler = authHandler;
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
        photoView.setTag(position);

        displayImage(photoView, _photoList.get(position));

        container.addView(photoView);

        return photoView;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((PhotoView) object);
    }


    public void dispose() {
        _disposables.dispose();
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


    private void downloadImage(final PhotoView view, final Photo photo, PhotoSize size) {
        _disposables.add(Flowable.fromCallable(() -> _dataServices.downloadPhoto(photo, size))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        x -> {
                            displayImage(view, photo);
                            _activity.updateProgress();
                        },
                        _authHandler::handleException
                )
        );

        _activity.updateProgress();
    }
}
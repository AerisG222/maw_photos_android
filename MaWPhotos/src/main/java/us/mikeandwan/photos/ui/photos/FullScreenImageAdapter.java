package us.mikeandwan.photos.ui.photos;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import com.github.chrisbanes.photoview.PhotoView;
import us.mikeandwan.photos.models.Photo;
import us.mikeandwan.photos.models.PhotoSize;
import us.mikeandwan.photos.services.DataServices;


// http://stackoverflow.com/questions/11306037/how-to-implement-zoom-pan-and-drag-on-viewpager-in-android
public class FullScreenImageAdapter extends PagerAdapter {
    private final CompositeDisposable _disposables = new CompositeDisposable();
    private final Context _context;
    private final IPhotoActivity _activity;
    private final DataServices _dataServices;
    private List<Photo> _photoList;


    public FullScreenImageAdapter(IPhotoActivity activity, DataServices dataServices) {
        _context = (Context)activity;
        _activity = activity;
        _dataServices = dataServices;
    }


    @Override
    public int getCount() {
        return _photoList.size();
    }


    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        PhotoView photoView = new PhotoView(container.getContext());
        photoView.setTag(position);

        displayImage(photoView, _photoList.get(position));

        container.addView(photoView);

        return photoView;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((PhotoView) object);
    }


    public void dispose() {
        _disposables.dispose();
    }


    public void refreshPhotoList() {
        _photoList = _activity.getPhotoList();
        notifyDataSetChanged();
    }


    private void displayImage(PhotoView view, Photo photo) {
        _disposables.add(Flowable.fromCallable(() -> {
                    _activity.addWork();
                    return _dataServices.downloadPhoto(photo, PhotoSize.Md);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        x -> {
                            _activity.removeWork();

                            Picasso
                                .get()
                                .load(x)
                                .into(view);
                        },
                        ex -> {
                            _activity.removeWork();
                            _activity.onApiException(ex);
                        }
                )
        );
    }
}

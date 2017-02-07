package us.mikeandwan.photos.ui.photos;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.models.Photo;
import us.mikeandwan.photos.models.PhotoSize;
import us.mikeandwan.photos.services.AuthenticationExceptionHandler;
import us.mikeandwan.photos.services.PhotoStorage;
import us.mikeandwan.photos.tasks.DownloadPhotoTask;


public class ThumbnailRecyclerAdapter extends RecyclerView.Adapter<ThumbnailRecyclerAdapter.ViewHolder> {
    private final CompositeDisposable _disposables = new CompositeDisposable();
    private final Context _context;
    private final PhotoStorage _photoStorage;
    private final DownloadPhotoTask _downloadPhotoTask;
    private final AuthenticationExceptionHandler _authHandler;
    private List<Photo> _photoList;


    public ThumbnailRecyclerAdapter(Context context, PhotoStorage photoStorage, DownloadPhotoTask downloadPhotoTask, AuthenticationExceptionHandler authHandler) {
        _context = context;
        _photoStorage = photoStorage;
        _downloadPhotoTask = downloadPhotoTask;
        _authHandler = authHandler;
    }


    @Override
    public ThumbnailRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        ImageView view = new ImageView(context);

        view.setScaleType(ImageView.ScaleType.CENTER);
        view.setPadding(2, 0, 2, 0);

        return new ThumbnailRecyclerAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ThumbnailRecyclerAdapter.ViewHolder viewHolder, int position) {
        Photo photo = _photoList.get(position);

        if (_photoStorage.doesExist(photo.getXsInfo().getPath())) {
            displayPhoto(photo, viewHolder);
        } else {
            viewHolder._thumbnailImageView.setImageBitmap(_photoStorage.getPlaceholderThumbnail());

            _disposables.add(Flowable.fromCallable(() -> _downloadPhotoTask.call(photo, PhotoSize.Xs))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            x -> displayPhoto(photo, viewHolder),
                            _authHandler::handleException
                    )
            );
        }
    }


    @Override
    public int getItemCount() {
        return _photoList.size();
    }


    public void setPhotoList(List<Photo> photoList) {
        _photoList = photoList;
    }


    public void dispose() {
        _disposables.dispose();
    }


    private void displayPhoto(Photo photo, ThumbnailRecyclerAdapter.ViewHolder viewHolder) {
        String file = "file://" + _photoStorage.getCachePath(photo.getXsInfo().getPath());

        Picasso
                .with(_context)
                .load(file)
                .resizeDimen(R.dimen.photo_list_thumbnail_size, R.dimen.photo_list_thumbnail_size)
                .centerCrop()
                .into(viewHolder._thumbnailImageView);
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView _thumbnailImageView;

        ViewHolder(View itemView) {
            super(itemView);

            _thumbnailImageView = (ImageView) itemView;
        }
    }
}
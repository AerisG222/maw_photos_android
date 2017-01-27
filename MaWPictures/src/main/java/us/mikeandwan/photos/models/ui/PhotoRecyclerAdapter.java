package us.mikeandwan.photos.models.ui;

import android.content.Context;
import android.content.Intent;
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
import us.mikeandwan.photos.activities.LoginActivity;
import us.mikeandwan.photos.models.Photo;
import us.mikeandwan.photos.models.PhotoSize;
import us.mikeandwan.photos.services.MawAuthenticationException;
import us.mikeandwan.photos.services.PhotoStorage;
import us.mikeandwan.photos.tasks.DownloadPhotoTask;


public class PhotoRecyclerAdapter extends RecyclerView.Adapter<PhotoRecyclerAdapter.ViewHolder> {
    private final CompositeDisposable _disposables = new CompositeDisposable();
    private final Context _context;
    private final List<Photo> _photoList;
    private final PhotoStorage _photoStorage;
    private final DownloadPhotoTask _downloadPhotoTask;


    public PhotoRecyclerAdapter(Context context, PhotoStorage photoStorage, DownloadPhotoTask downloadPhotoTask, List<Photo> photoList) {
        _context = context;
        _photoStorage = photoStorage;
        _photoList = photoList;
        _downloadPhotoTask = downloadPhotoTask;
    }


    @Override
    public PhotoRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        ImageView view = new ImageView(context);

        return new PhotoRecyclerAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(PhotoRecyclerAdapter.ViewHolder viewHolder, int position) {
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
                            ex -> handleException(ex)
                    )
            );
        }
    }


    @Override
    public int getItemCount() {
        return _photoList.size();
    }


    private void displayPhoto(Photo photo, PhotoRecyclerAdapter.ViewHolder viewHolder) {
        String file = "file://" + _photoStorage.getCachePath(photo.getXsInfo().getPath());

        Picasso
                .with(_context)
                .load(file)
                .resizeDimen(R.dimen.category_list_thumbnail_size, R.dimen.category_list_thumbnail_size)
                .centerCrop()
                .into(viewHolder._thumbnailImageView);
    }


    private void handleException(Throwable ex) {
        if (ex.getCause() instanceof MawAuthenticationException) {
            _context.startActivity(new Intent(_context, LoginActivity.class));
        }
    }


    public void dispose() {
        _disposables.dispose();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView _thumbnailImageView;

        public ViewHolder(View itemView) {
            super(itemView);

            _thumbnailImageView = (ImageView) itemView;
        }
    }
}

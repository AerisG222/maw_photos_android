package com.example.touch;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.activities.IPhotoActivity;
import us.mikeandwan.photos.activities.LoginActivity;
import us.mikeandwan.photos.models.Photo;
import us.mikeandwan.photos.models.PhotoDownload;
import us.mikeandwan.photos.models.PhotoSize;
import us.mikeandwan.photos.services.MawAuthenticationException;
import us.mikeandwan.photos.services.PhotoApiClient;
import us.mikeandwan.photos.services.PhotoStorage;
import us.mikeandwan.photos.tasks.DownloadImageBackgroundTask;


// http://stackoverflow.com/questions/11306037/how-to-implement-zoom-pan-and-drag-on-viewpager-in-android
public class FullScreenImageAdapter extends PagerAdapter {
    private final Context _context;
    private final IPhotoActivity _activity;
    private final List<Photo> _photoList;
    private final Dictionary<Integer, TouchImageView> _imgList = new Hashtable<>();
    private final LayoutInflater _inflater;
    private final PhotoStorage _photoStorage;
    private final DownloadImageBackgroundTask _downloadImageTask;


    public FullScreenImageAdapter(Context context, PhotoStorage photoStorage, PhotoApiClient photoClient, IPhotoActivity activity) {
        _context = context;
        _activity = activity;
        _photoList = activity.getPhotoList();
        _photoStorage = photoStorage;
        _inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        _downloadImageTask = new DownloadImageBackgroundTask();
        _downloadImageTask.setPhotoClient(photoClient);
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
        View v = _inflater.inflate(R.layout.fullscreen_image, container, false);

        v.setTag(position);

        TouchImageView img = (TouchImageView) v.findViewById(R.id.imgDisplay);

        _imgList.put(position, img);
        PhotoDownload pd = new PhotoDownload(_photoList.get(position), position);
        displayImage(pd);

        container.addView(v);

        return v;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
        _imgList.remove(position);
    }


    private void displayImage(PhotoDownload photoDownload) {
        String path = photoDownload.getMawPhoto().getMdInfo().getPath();

        if (!_photoStorage.doesExist(path)) {
            if (photoDownload.getDownloadAttempts() == 0) {
                photoDownload.incrementDownloadCount();
                downloadImage(photoDownload, PhotoSize.Md);
            } else {
                Log.w(MawApplication.LOG_TAG, "we have already tried to download this main image w/o success, not trying again");
            }
        } else {
            BitmapDrawable drawable = new BitmapDrawable(_context.getResources(), _photoStorage.get(path));
            TouchImageView img = _imgList.get(photoDownload.getIndex());

            if (img != null) {
                img.setImageDrawable(drawable);
            }
        }
    }


    private void handleException(Throwable ex) {
        if (ex.getCause() instanceof MawAuthenticationException) {
            _context.startActivity(new Intent(_context, LoginActivity.class));
        }
    }


    private void downloadImage(final PhotoDownload photoDownload, PhotoSize size) {
        Flowable.fromCallable(() -> _downloadImageTask.call(photoDownload, size))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .subscribe(
                        x -> {
                            displayImage(x);
                            _activity.updateProgress();
                        },
                        ex -> handleException(ex)
                );

        _activity.updateProgress();
    }
}
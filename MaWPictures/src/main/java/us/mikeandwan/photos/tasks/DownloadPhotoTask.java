package us.mikeandwan.photos.tasks;

import android.util.Log;

import javax.inject.Inject;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.models.Photo;
import us.mikeandwan.photos.models.PhotoSize;
import us.mikeandwan.photos.services.PhotoApiClient;


public class DownloadPhotoTask {
    private PhotoApiClient _client;


    @Inject
    public DownloadPhotoTask(PhotoApiClient client) {
        _client = client;
    }


    public boolean call(Photo photo, PhotoSize size) throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started to download photo: " + photo.getId());

        if (!_client.isConnected()) {
            throw new Exception("Network unavailable");
        }

        switch (size) {
            case Sm:
                _client.downloadPhoto(photo.getSmInfo().getPath());
                break;
            case Md:
                _client.downloadPhoto(photo.getMdInfo().getPath());
                break;
            case Xs:
                _client.downloadPhoto(photo.getXsInfo().getPath());
                break;
            case Lg:
                _client.downloadPhoto(photo.getLgInfo().getPath());
                break;
        }

        return true;
    }
}

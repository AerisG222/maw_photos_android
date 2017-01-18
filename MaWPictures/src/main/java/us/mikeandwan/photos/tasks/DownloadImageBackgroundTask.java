package us.mikeandwan.photos.tasks;

import android.util.Log;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.data.PhotoDownload;
import us.mikeandwan.photos.data.PhotoSize;
import us.mikeandwan.photos.services.PhotoApiClient;


public class DownloadImageBackgroundTask {
    @Bean
    PhotoApiClient _client;

    public void setPhotoClient(PhotoApiClient client) {
        _client = client;
    }

    public PhotoDownload call(PhotoDownload photoDownload, PhotoSize size) throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started to download photo: " + photoDownload.getMawPhoto().getId());

        if (!_client.isConnected()) {
            throw new Exception("Network unavailable");
        }

        switch (size) {
            case Sm:
                _client.downloadPhoto(photoDownload.getMawPhoto().getSmInfo().getPath());
                break;
            case Md:
                _client.downloadPhoto(photoDownload.getMawPhoto().getMdInfo().getPath());
                break;
            case Xs:
                _client.downloadPhoto(photoDownload.getMawPhoto().getXsInfo().getPath());
                break;
            case Lg:
                _client.downloadPhoto(photoDownload.getMawPhoto().getLgInfo().getPath());
                break;
        }

        return photoDownload;
    }
}

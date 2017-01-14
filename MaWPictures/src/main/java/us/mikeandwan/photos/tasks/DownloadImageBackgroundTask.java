package us.mikeandwan.photos.tasks;

import android.util.Log;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.data.PhotoDownload;
import us.mikeandwan.photos.data.PhotoSize;
import us.mikeandwan.photos.services.PhotoApiClient;


public class DownloadImageBackgroundTask extends BackgroundTask<PhotoDownload> {
    private final PhotoDownload _photoDownload;
    private final PhotoSize _size;
    private final PhotoApiClient _client;


    public DownloadImageBackgroundTask(PhotoApiClient client, PhotoDownload photoDownload, PhotoSize size, BackgroundTaskPriority priority) {
        super(priority);

        _client = client;
        _photoDownload = photoDownload;
        _size = size;
    }


    @Override
    public PhotoDownload call() throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started to download photo: " + _photoDownload.getMawPhoto().getId());

        if (!_client.isConnected()) {
            throw new Exception("Network unavailable");
        }

        switch (_size) {
            case Sm:
                _client.downloadPhoto(_photoDownload.getMawPhoto().getSmInfo().getPath());
                break;
            case Md:
                _client.downloadPhoto(_photoDownload.getMawPhoto().getMdInfo().getPath());
                break;
            case Xs:
                _client.downloadPhoto(_photoDownload.getMawPhoto().getXsInfo().getPath());
                break;
            case Lg:
                _client.downloadPhoto(_photoDownload.getMawPhoto().getLgInfo().getPath());
                break;
        }

        return _photoDownload;
    }
}

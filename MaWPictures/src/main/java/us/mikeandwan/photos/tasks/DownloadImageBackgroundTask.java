package us.mikeandwan.photos.tasks;

import android.content.Context;
import android.util.Log;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.data.PhotoDownload;
import us.mikeandwan.photos.data.PhotoSize;
import us.mikeandwan.photos.services.PhotoApiClient;


public class DownloadImageBackgroundTask extends BackgroundTask<PhotoDownload> {
    private final Context _context;
    private final PhotoDownload _photoDownload;
    private final PhotoSize _size;


    public DownloadImageBackgroundTask(Context context, PhotoDownload photoDownload, PhotoSize size, BackgroundTaskPriority priority) {
        super(priority);

        _context = context;
        _photoDownload = photoDownload;
        _size = size;
    }


    @Override
    public PhotoDownload call() throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started to download photo: " + _photoDownload.getMawPhoto().getId());

        PhotoApiClient client = new PhotoApiClient(_context);

        if (!client.isConnected(_context)) {
            throw new Exception("Network unavailable");
        }

        switch (_size) {
            case Sm:
                client.downloadPhoto(_photoDownload.getMawPhoto().getSmInfo().getPath());
                break;
            case Md:
                client.downloadPhoto(_photoDownload.getMawPhoto().getMdInfo().getPath());
                break;
            case Xs:
                client.downloadPhoto(_photoDownload.getMawPhoto().getXsInfo().getPath());
                break;
            case Lg:
                client.downloadPhoto(_photoDownload.getMawPhoto().getLgInfo().getPath());
                break;
        }

        return _photoDownload;
    }
}

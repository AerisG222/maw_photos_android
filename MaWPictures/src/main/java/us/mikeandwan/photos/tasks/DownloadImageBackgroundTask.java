package us.mikeandwan.photos.tasks;

import android.content.Context;
import android.util.Log;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.data.PhotoDownload;
import us.mikeandwan.photos.data.PhotoSize;
import us.mikeandwan.photos.services.PhotoApiClient;


public class DownloadImageBackgroundTask extends BackgroundTask<PhotoDownload> {
    private Context _context;
    private PhotoDownload _photoDownload;
    private PhotoSize _size;


    public DownloadImageBackgroundTask(Context context, PhotoDownload photoDownload, PhotoSize size) {
        this(context, photoDownload, size, BackgroundTaskPriority.Normal);
    }


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
            case Full:
                client.downloadPhoto(_photoDownload.getMawPhoto().getFullsizeInfo().getPath());
                break;
            case Fuller:
                client.downloadPhoto(_photoDownload.getMawPhoto().getFullerInfo().getPath());
                break;
            case Thumbnail:
                client.downloadPhoto(_photoDownload.getMawPhoto().getThumbnailInfo().getPath());
                break;
            case Original:
                client.downloadPhoto(_photoDownload.getMawPhoto().getOriginalInfo().getPath());
                break;
        }

        return _photoDownload;
    }
}

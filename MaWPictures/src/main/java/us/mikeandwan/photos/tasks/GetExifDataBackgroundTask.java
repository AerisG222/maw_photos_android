package us.mikeandwan.photos.tasks;

import android.util.Log;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.data.ExifData;
import us.mikeandwan.photos.services.PhotoApiClient;


public class GetExifDataBackgroundTask extends BackgroundTask<ExifData> {
    private final int _photoId;
    private final PhotoApiClient _client;


    public GetExifDataBackgroundTask(PhotoApiClient client, int photoId) {
        super(BackgroundTaskPriority.High);

        _client = client;
        _photoId = photoId;
    }


    @Override
    public ExifData call() throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started to get exif data for photo: " + _photoId);

        if (!_client.isConnected()) {
            throw new Exception("Network unavailable");
        }

        return _client.getExifData(_photoId);
    }
}

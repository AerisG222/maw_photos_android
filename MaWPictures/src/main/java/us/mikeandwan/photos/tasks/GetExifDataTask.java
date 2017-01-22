package us.mikeandwan.photos.tasks;

import android.util.Log;

import javax.inject.Inject;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.models.ExifData;
import us.mikeandwan.photos.services.PhotoApiClient;


public class GetExifDataTask {
    private PhotoApiClient _client;


    @Inject
    public GetExifDataTask(PhotoApiClient client) {
        _client = client;
    }


    public ExifData call(int photoId) throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started to get exif data for photo: " + photoId);

        if (!_client.isConnected()) {
            throw new Exception("Network unavailable");
        }

        return _client.getExifData(photoId);
    }
}

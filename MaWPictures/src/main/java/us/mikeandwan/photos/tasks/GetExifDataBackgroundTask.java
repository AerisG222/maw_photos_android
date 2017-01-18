package us.mikeandwan.photos.tasks;

import android.util.Log;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.data.ExifData;
import us.mikeandwan.photos.services.PhotoApiClient;


public class GetExifDataBackgroundTask {
    @Bean
    PhotoApiClient _client;


    public ExifData call(int photoId) throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started to get exif data for photo: " + photoId);

        if (!_client.isConnected()) {
            throw new Exception("Network unavailable");
        }

        return _client.getExifData(photoId);
    }
}

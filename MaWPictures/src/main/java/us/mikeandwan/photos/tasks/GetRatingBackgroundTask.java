package us.mikeandwan.photos.tasks;

import android.util.Log;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.data.Rating;
import us.mikeandwan.photos.services.PhotoApiClient;


public class GetRatingBackgroundTask {
    @Bean
    PhotoApiClient _client;


    public Rating call(int photoId) throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started to get rating for photo: " + photoId);

        if (!_client.isConnected()) {
            throw new Exception("Network unavailable");
        }

        return _client.getRatings(photoId);
    }
}

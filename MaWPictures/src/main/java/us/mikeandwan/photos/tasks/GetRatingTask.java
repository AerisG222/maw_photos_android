package us.mikeandwan.photos.tasks;

import android.util.Log;

import javax.inject.Inject;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.models.Rating;
import us.mikeandwan.photos.services.PhotoApiClient;


public class GetRatingTask {
    private PhotoApiClient _client;


    @Inject
    public GetRatingTask(PhotoApiClient client) {
        _client = client;
    }


    public Rating call(int photoId) throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started to get rating for photo: " + photoId);

        if (!_client.isConnected()) {
            throw new Exception("Network unavailable");
        }

        return _client.getRatings(photoId);
    }
}

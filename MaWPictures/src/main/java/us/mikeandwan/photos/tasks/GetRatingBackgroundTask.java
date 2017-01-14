package us.mikeandwan.photos.tasks;

import android.util.Log;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.data.Rating;
import us.mikeandwan.photos.services.PhotoApiClient;


public class GetRatingBackgroundTask extends BackgroundTask<Rating> {
    private final int _photoId;
    private final PhotoApiClient _client;


    public GetRatingBackgroundTask(PhotoApiClient client, int photoId) {
        super(BackgroundTaskPriority.High);

        _client = client;
        _photoId = photoId;
    }


    @Override
    public Rating call() throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started to get rating for photo: " + _photoId);

        if (!_client.isConnected()) {
            throw new Exception("Network unavailable");
        }

        return _client.getRatings(_photoId);
    }
}

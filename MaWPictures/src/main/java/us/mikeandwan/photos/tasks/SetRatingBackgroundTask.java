package us.mikeandwan.photos.tasks;

import android.util.Log;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.data.Rating;
import us.mikeandwan.photos.services.PhotoApiClient;


public class SetRatingBackgroundTask extends BackgroundTask<Rating> {
    private final PhotoApiClient _client;
    private final int _photoId;
    private final int _rating;


    public SetRatingBackgroundTask(PhotoApiClient client, int photoId, int rating) {
        super(BackgroundTaskPriority.High);

        _client = client;
        _photoId = photoId;
        _rating = rating;
    }


    @Override
    public Rating call() throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started to set user rating for photo: " + _photoId);

        if (!_client.isConnected()) {
            throw new Exception("Network unavailable");
        }

        Float averageRating = _client.setRating(_photoId, _rating);

        Rating rating = new Rating();

        if (averageRating != null) {
            rating.setAverageRating(averageRating);
            rating.setUserRating((short) Math.round(_rating));
        } else {
            rating.setAverageRating((float) 0);
            rating.setUserRating((short) 0);
        }

        return rating;
    }
}

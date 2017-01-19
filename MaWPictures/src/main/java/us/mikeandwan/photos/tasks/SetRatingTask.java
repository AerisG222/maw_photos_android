package us.mikeandwan.photos.tasks;

import android.util.Log;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.models.Rating;
import us.mikeandwan.photos.services.PhotoApiClient;


public class SetRatingTask {
    @Bean
    PhotoApiClient _client;


    public Rating call(int photoId, int rating) throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started to set user rating for photo: " + photoId);

        if (!_client.isConnected()) {
            throw new Exception("Network unavailable");
        }

        Float averageRating = _client.setRating(photoId, rating);

        Rating rate = new Rating();

        if (averageRating != null) {
            rate.setAverageRating(averageRating);
            rate.setUserRating((short) Math.round(rating));
        } else {
            rate.setAverageRating((float) 0);
            rate.setUserRating((short) 0);
        }

        return rate;
    }
}

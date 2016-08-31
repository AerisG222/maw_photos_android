package us.mikeandwan.photos.tasks;


import android.content.Context;
import android.util.Log;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.data.Rating;
import us.mikeandwan.photos.services.PhotoApiClient;

public class SetRatingBackgroundTask extends BackgroundTask<Rating> {
    private final Context _context;
    private final int _photoId;
    private final int _rating;


    public SetRatingBackgroundTask(Context context, int photoId, int rating) {
        super(BackgroundTaskPriority.High);

        _context = context;
        _photoId = photoId;
        _rating = rating;
    }


    @Override
    public Rating call() throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started to set user rating for photo: " + _photoId);

        PhotoApiClient client = new PhotoApiClient(_context);

        if (!client.isConnected(_context)) {
            throw new Exception("Network unavailable");
        }

        Float averageRating = client.setRating(_photoId, _rating);

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

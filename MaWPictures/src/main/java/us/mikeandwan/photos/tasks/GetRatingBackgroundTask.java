package us.mikeandwan.photos.tasks;


import android.content.Context;
import android.util.Log;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.data.Rating;
import us.mikeandwan.photos.services.PhotoApiClient;

public class GetRatingBackgroundTask extends BackgroundTask<Rating> {
    private Context _context;
    private int _photoId;


    public GetRatingBackgroundTask(Context context, int photoId) {
        super(BackgroundTaskPriority.High);

        _context = context;
        _photoId = photoId;
    }


    @Override
    public Rating call() throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started to get rating for photo: " + _photoId);

        PhotoApiClient client = new PhotoApiClient(_context);

        if (!client.isConnected(_context)) {
            throw new Exception("Network unavailable");
        }

        return client.getRatings(_photoId);
    }
}

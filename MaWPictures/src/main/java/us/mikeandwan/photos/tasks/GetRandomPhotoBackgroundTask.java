package us.mikeandwan.photos.tasks;

import android.content.Context;
import android.util.Log;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.data.PhotoAndCategory;
import us.mikeandwan.photos.services.PhotoApiClient;


public class GetRandomPhotoBackgroundTask extends BackgroundTask<PhotoAndCategory> {
    private final Context _context;


    public GetRandomPhotoBackgroundTask(Context context) {
        super(BackgroundTaskPriority.Normal);

        _context = context;
    }


    @Override
    public PhotoAndCategory call() throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started to get random photo");

        PhotoApiClient client = new PhotoApiClient(_context);

        if (!client.isConnected(_context)) {
            throw new Exception("Network unavailable");
        }

        return client.getRandomPhoto();
    }
}

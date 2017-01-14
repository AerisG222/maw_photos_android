package us.mikeandwan.photos.tasks;

import android.util.Log;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.data.PhotoAndCategory;
import us.mikeandwan.photos.services.PhotoApiClient;


public class GetRandomPhotoBackgroundTask extends BackgroundTask<PhotoAndCategory> {
    private final PhotoApiClient _client;


    public GetRandomPhotoBackgroundTask(PhotoApiClient client) {
        super(BackgroundTaskPriority.Normal);

        _client = client;
    }


    @Override
    public PhotoAndCategory call() throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started to get random photo");

        if (!_client.isConnected()) {
            throw new Exception("Network unavailable");
        }

        return _client.getRandomPhoto();
    }
}

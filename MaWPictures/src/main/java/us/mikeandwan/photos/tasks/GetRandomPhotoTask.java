package us.mikeandwan.photos.tasks;

import android.util.Log;

import javax.inject.Inject;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.models.PhotoAndCategory;
import us.mikeandwan.photos.services.PhotoApiClient;


public class GetRandomPhotoTask {
    private PhotoApiClient _client;


    @Inject
    public GetRandomPhotoTask(PhotoApiClient client) {
        _client = client;
    }


    public PhotoAndCategory call() throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started to get random photo");

        if (!_client.isConnected()) {
            throw new Exception("Network unavailable");
        }

        return _client.getRandomPhoto();
    }
}

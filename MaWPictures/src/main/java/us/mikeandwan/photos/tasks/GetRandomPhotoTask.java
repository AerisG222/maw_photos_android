package us.mikeandwan.photos.tasks;

import android.util.Log;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.models.PhotoAndCategory;
import us.mikeandwan.photos.services.PhotoApiClient;


public class GetRandomPhotoTask {
    @Bean
    PhotoApiClient _client;


    public PhotoAndCategory call() throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started to get random photo");

        if (!_client.isConnected()) {
            throw new Exception("Network unavailable");
        }

        return _client.getRandomPhoto();
    }
}

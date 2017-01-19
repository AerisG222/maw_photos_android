package us.mikeandwan.photos.tasks;

import android.util.Log;

import java.util.List;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.models.Photo;
import us.mikeandwan.photos.services.PhotoApiClient;


public class GetPhotoListBackgroundTask {
    @Bean
    PhotoApiClient _client;


    public List<Photo> call(String url) throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started to get photo list");

        if (!_client.isConnected()) {
            throw new Exception("Network unavailable");
        }

        return _client.getPhotos(url);
    }
}

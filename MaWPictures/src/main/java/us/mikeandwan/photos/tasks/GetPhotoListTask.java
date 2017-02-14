package us.mikeandwan.photos.tasks;

import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.models.Photo;
import us.mikeandwan.photos.services.PhotoApiClient;
import us.mikeandwan.photos.services.PhotoListType;


public class GetPhotoListTask {
    private PhotoApiClient _client;


    @Inject
    public GetPhotoListTask(PhotoApiClient client) {
        _client = client;
    }


    public List<Photo> call(PhotoListType type, int categoryId) throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started to get photo list");

        if (!_client.isConnected()) {
            throw new Exception("Network unavailable");
        }

        return _client.getPhotos(type, categoryId);
    }
}

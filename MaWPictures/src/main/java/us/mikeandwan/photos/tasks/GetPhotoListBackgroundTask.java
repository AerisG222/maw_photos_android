package us.mikeandwan.photos.tasks;

import android.util.Log;

import java.util.List;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.data.Photo;
import us.mikeandwan.photos.services.PhotoApiClient;


public class GetPhotoListBackgroundTask extends BackgroundTask<List<Photo>> {
    private final String _url;
    PhotoApiClient _client;


    public GetPhotoListBackgroundTask(PhotoApiClient client, String url) {
        super(BackgroundTaskPriority.Normal);

        _client = client;
        _url = url;
    }


    @Override
    public List<Photo> call() throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started to get photo list");

        if (!_client.isConnected()) {
            throw new Exception("Network unavailable");
        }

        return _client.getPhotos(_url);
    }
}

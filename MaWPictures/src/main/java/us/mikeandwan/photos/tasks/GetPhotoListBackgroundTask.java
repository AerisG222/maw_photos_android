package us.mikeandwan.photos.tasks;

import android.content.Context;
import android.util.Log;

import java.util.List;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.data.Photo;
import us.mikeandwan.photos.services.PhotoApiClient;


public class GetPhotoListBackgroundTask extends BackgroundTask<List<Photo>> {
    private final Context _context;
    private final String _url;


    public GetPhotoListBackgroundTask(Context context, String url) {
        super(BackgroundTaskPriority.Normal);

        _context = context;
        _url = url;
    }


    @Override
    public List<Photo> call() throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started to get photo list");

        PhotoApiClient client = new PhotoApiClient(_context);

        if (!client.isConnected(_context)) {
            throw new Exception("Network unavailable");
        }

        return client.getPhotos(_url);
    }
}

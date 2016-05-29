package us.mikeandwan.photos.tasks;


import android.content.Context;
import android.util.Log;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.data.ExifData;
import us.mikeandwan.photos.services.PhotoApiClient;

public class GetExifDataBackgroundTask extends BackgroundTask<ExifData> {
    private Context _context;
    private int _photoId;


    public GetExifDataBackgroundTask(Context context, int photoId) {
        super(BackgroundTaskPriority.High);

        _context = context;
        _photoId = photoId;
    }


    @Override
    public ExifData call() throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started to get exif data for photo: " + _photoId);

        PhotoApiClient client = new PhotoApiClient(_context);

        if (!client.isConnected(_context)) {
            throw new Exception("Network unavailable");
        }

        return client.getExifData(_photoId);
    }
}

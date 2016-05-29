package us.mikeandwan.photos.tasks;


import android.content.Context;
import android.util.Log;

import java.util.List;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.data.Comment;
import us.mikeandwan.photos.services.PhotoApiClient;

public class GetCommentsBackgroundTask extends BackgroundTask<List<Comment>> {
    private Context _context;
    private int _photoId;


    public GetCommentsBackgroundTask(Context context, int photoId) {
        super(BackgroundTaskPriority.High);

        _context = context;
        _photoId = photoId;
    }


    @Override
    public List<Comment> call() throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started to get comments for photo: " + _photoId);

        PhotoApiClient client = new PhotoApiClient(_context);

        if (!client.isConnected(_context)) {
            throw new Exception("Network unavailable");
        }

        return client.getComments(_photoId);
    }
}

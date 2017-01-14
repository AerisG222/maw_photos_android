package us.mikeandwan.photos.tasks;

import android.util.Log;

import java.util.List;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.data.Comment;
import us.mikeandwan.photos.services.PhotoApiClient;


public class GetCommentsBackgroundTask extends BackgroundTask<List<Comment>> {
    private final int _photoId;
    private final PhotoApiClient _client;


    public GetCommentsBackgroundTask(PhotoApiClient client, int photoId) {
        super(BackgroundTaskPriority.High);

        _client = client;
        _photoId = photoId;
    }


    @Override
    public List<Comment> call() throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started to get comments for photo: " + _photoId);

        if (!_client.isConnected()) {
            throw new Exception("Network unavailable");
        }

        return _client.getComments(_photoId);
    }
}

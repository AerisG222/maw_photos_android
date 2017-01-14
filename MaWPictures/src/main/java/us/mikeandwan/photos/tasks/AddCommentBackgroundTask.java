package us.mikeandwan.photos.tasks;

import android.util.Log;

import java.util.List;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.data.Comment;
import us.mikeandwan.photos.data.CommentPhoto;
import us.mikeandwan.photos.services.PhotoApiClient;


public class AddCommentBackgroundTask extends BackgroundTask<List<Comment>> {
    private final CommentPhoto _cp;
    private final PhotoApiClient _client;


    public AddCommentBackgroundTask(PhotoApiClient client, CommentPhoto cp) {
        super(BackgroundTaskPriority.High);

        _client = client;
        _cp = cp;
    }


    @Override
    public List<Comment> call() throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started to add comment for photo: " + _cp.getPhotoId());

        if (!_client.isConnected()) {
            throw new Exception("Network unavailable");
        }

        _client.addComment(_cp.getPhotoId(), _cp.getComment());

        // now request all comments again to confirm that it was added
        return _client.getComments(_cp.getPhotoId());
    }
}

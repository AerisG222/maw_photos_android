package us.mikeandwan.photos.tasks;


import android.content.Context;
import android.util.Log;

import java.util.List;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.data.Comment;
import us.mikeandwan.photos.data.CommentPhoto;
import us.mikeandwan.photos.services.PhotoApiClient;

public class AddCommentBackgroundTask extends BackgroundTask<List<Comment>> {
    private Context _context;
    private CommentPhoto _cp;


    public AddCommentBackgroundTask(Context context, CommentPhoto cp) {
        super(BackgroundTaskPriority.High);

        _context = context;
        _cp = cp;
    }


    @Override
    public List<Comment> call() throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started to add comment for photo: " + _cp.getPhotoId());

        PhotoApiClient client = new PhotoApiClient(_context);

        if (!client.isConnected(_context)) {
            throw new Exception("Network unavailable");
        }

        client.addComment(_cp.getPhotoId(), _cp.getComment());

        // now request all comments again to confirm that it was added
        return client.getComments(_cp.getPhotoId());
    }
}

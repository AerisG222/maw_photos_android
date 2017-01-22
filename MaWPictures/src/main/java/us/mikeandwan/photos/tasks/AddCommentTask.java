package us.mikeandwan.photos.tasks;

import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.models.Comment;
import us.mikeandwan.photos.models.CommentPhoto;
import us.mikeandwan.photos.services.PhotoApiClient;


public class AddCommentTask {
    private PhotoApiClient _client;


    @Inject
    public AddCommentTask(PhotoApiClient client) {
        _client = client;
    }


    public List<Comment> call(CommentPhoto cp) throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started to add comment for photo: " + cp.getPhotoId());

        if (!_client.isConnected()) {
            throw new Exception("Network unavailable");
        }

        _client.addComment(cp.getPhotoId(), cp.getComment());

        // now request all comments again to confirm that it was added
        return _client.getComments(cp.getPhotoId());
    }
}

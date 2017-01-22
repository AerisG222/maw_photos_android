package us.mikeandwan.photos.tasks;

import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.models.Comment;
import us.mikeandwan.photos.services.PhotoApiClient;


public class GetCommentsTask {
    private PhotoApiClient _client;


    @Inject
    public GetCommentsTask(PhotoApiClient client) {
        _client = client;
    }


    public List<Comment> call(int photoId) throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started to get comments for photo: " + photoId);

        if (!_client.isConnected()) {
            throw new Exception("Network unavailable");
        }

        return _client.getComments(photoId);
    }
}

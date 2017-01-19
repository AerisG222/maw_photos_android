package us.mikeandwan.photos.tasks;

import android.util.Log;

import java.util.List;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.models.Comment;
import us.mikeandwan.photos.services.PhotoApiClient;


public class GetCommentsTask {
    @Bean
    PhotoApiClient _client;


    public List<Comment> call(int photoId) throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started to get comments for photo: " + photoId);

        if (!_client.isConnected()) {
            throw new Exception("Network unavailable");
        }

        return _client.getComments(photoId);
    }
}

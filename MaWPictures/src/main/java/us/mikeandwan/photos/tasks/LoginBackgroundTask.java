package us.mikeandwan.photos.tasks;


import android.content.Context;
import android.util.Log;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.data.Credentials;
import us.mikeandwan.photos.services.PhotoApiClient;

public class LoginBackgroundTask extends BackgroundTask<Boolean> {
    private Credentials _creds;
    private Context _context;


    public LoginBackgroundTask(Context context, Credentials creds) {
        super(BackgroundTaskPriority.VeryHigh);

        _context = context;
        _creds = creds;
    }


    @Override
    public Boolean call() throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started login task");

        PhotoApiClient client = new PhotoApiClient(_context);

        if (!client.isConnected(_context)) {
            Log.e(MawApplication.LOG_TAG, "network unavailable");
            return false;
        }

        if (client.authenticate(_creds.getUsername(), _creds.getPassword())) {
            Log.i(MawApplication.LOG_TAG, "authenticated");

            return true;
        } else {
            Log.i(MawApplication.LOG_TAG, "authentication failed");

            return false;
        }
    }
}

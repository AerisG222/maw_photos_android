package us.mikeandwan.photos.tasks;

import android.util.Log;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.data.Credentials;
import us.mikeandwan.photos.services.PhotoApiClient;


public class LoginBackgroundTask {
    @Bean
    PhotoApiClient _client;


    public Boolean call(Credentials creds) throws Exception {
        Log.d(MawApplication.LOG_TAG, "> started login task");

        if (!_client.isConnected()) {
            Log.e(MawApplication.LOG_TAG, "network unavailable");
            return false;
        }

        if (_client.authenticate(creds.getUsername(), creds.getPassword())) {
            Log.i(MawApplication.LOG_TAG, "authenticated");

            return true;
        } else {
            Log.i(MawApplication.LOG_TAG, "authentication failed");

            return false;
        }
    }
}

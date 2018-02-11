package us.mikeandwan.photos.di;

import android.net.Uri;
import android.support.annotation.NonNull;

import net.openid.appauth.connectivity.ConnectionBuilder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.OkUrlFactory;


public class OkHttpConnectionBuilder implements ConnectionBuilder {
    OkHttpClient _client;

    public OkHttpConnectionBuilder(OkHttpClient okHttpClient) {
        _client = okHttpClient;
    }

    public HttpURLConnection openConnection(@NonNull Uri uri) throws IOException {
        OkUrlFactory factory = new OkUrlFactory(_client);
        URL url = new URL(uri.toString());

        return factory.open(url);
    }
}

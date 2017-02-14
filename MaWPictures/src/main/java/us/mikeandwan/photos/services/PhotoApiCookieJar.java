package us.mikeandwan.photos.services;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import us.mikeandwan.photos.MawApplication;


public class PhotoApiCookieJar implements CookieJar {
    private static final String AUTH_COOKIE_NAME = "maw_auth";
    private static final String XSRF_COOKIE_NAME = "XSRF-TOKEN";

    private final List<Cookie> _cookies = new ArrayList<>();


    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        if(_cookies.isEmpty()) {
            _cookies.addAll(cookies);
        }
        else {
            for(int i = 0; i < cookies.size(); i++) {
                for(int j = 0; j < _cookies.size(); j++) {
                    if(cookies.get(i).name().equalsIgnoreCase(_cookies.get(j).name())) {
                        _cookies.set(j, cookies.get(i));
                        break;
                    }
                }

                _cookies.add(cookies.get(i));
            }
        }
    }


    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        return _cookies;
    }


    public String getXsrfToken() {
        for(Cookie cookie : _cookies) {
            if (XSRF_COOKIE_NAME.equalsIgnoreCase(cookie.name())) {
                return cookie.value();
            }
        }

        return null;
    }


    boolean isAuthenticated() {
        boolean authCookieAvailable = false;
        boolean xsrfCookieAvailable = false;
        long now = System.currentTimeMillis();

        for(Cookie cookie : _cookies) {
            if (AUTH_COOKIE_NAME.equalsIgnoreCase(cookie.name()) && now < cookie.expiresAt()) {
                Log.i(MawApplication.LOG_TAG, "Auth cookie valid");
                authCookieAvailable = true;
            }
            else if (XSRF_COOKIE_NAME.equalsIgnoreCase(cookie.name()) && now < cookie.expiresAt()) {
                Log.i(MawApplication.LOG_TAG, "XSRF cookie valid");
                xsrfCookieAvailable = true;
            }
        }

        return authCookieAvailable && xsrfCookieAvailable;
    }
}

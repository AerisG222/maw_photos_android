package us.mikeandwan.photos.services;

import android.util.Log;

import androidx.annotation.NonNull;

import timber.log.Timber;

// https://github.com/JakeWharton/timber/blob/master/timber-sample/src/main/java/com/example/timber/ExampleApp.java
public class CrashReportingTree extends Timber.Tree {
    @Override protected void log(int priority, String tag, @NonNull String message, Throwable t) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return;
        }

        Log.println(priority, tag, message);

        if (t != null) {
            Log.w(tag, t);
        }
    }
}

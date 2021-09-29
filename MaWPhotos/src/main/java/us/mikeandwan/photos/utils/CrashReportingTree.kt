package us.mikeandwan.photos.utils

import android.util.Log
import timber.log.Timber.Tree

// https://github.com/JakeWharton/timber/blob/master/timber-sample/src/main/java/com/example/timber/ExampleApp.java
class CrashReportingTree : Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return
        }

        Log.println(priority, tag, message)

        if (t != null) {
            Log.w(tag, t)
        }
    }
}
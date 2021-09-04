package us.mikeandwan.photos

import dagger.hilt.android.HiltAndroidApp
import android.app.Application
import timber.log.Timber
import us.mikeandwan.photos.services.CrashReportingTree
import androidx.appcompat.app.AppCompatDelegate
import us.mikeandwan.photos.services.DataServices
import javax.inject.Inject

@HiltAndroidApp
class MawApplication : Application() {
    var notificationCount = 0

    @Inject lateinit var dataServices: DataServices

    override fun onCreate() {
        super.onCreate()
        instance = this;

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        dataServices.wipeTempFiles()
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID_NEW_CATEGORIES = "notify_new_categories"
        const val NOTIFICATION_CHANNEL_ID_UPLOAD_FILES = "files_uploaded"
        const val JOB_ID_UPDATE_CATEGORY = 2
        const val JOB_ID_UPLOAD_FILES = 3

        lateinit var instance: MawApplication
            private set
    }
}
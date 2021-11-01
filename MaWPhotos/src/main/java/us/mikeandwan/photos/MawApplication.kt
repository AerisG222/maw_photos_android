package us.mikeandwan.photos

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import us.mikeandwan.photos.utils.CrashReportingTree
import us.mikeandwan.photos.services.DataServices
import us.mikeandwan.photos.workers.UpdateCategoriesWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class MawApplication : Application(), Configuration.Provider {
    var notificationCount = 0

    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var dataServices: DataServices

    override fun onCreate() {
        super.onCreate()
        instance = this

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }

        dataServices.wipeTempFiles()

        schedulePeriodicRefresh()
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    private fun schedulePeriodicRefresh() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val work = PeriodicWorkRequestBuilder<UpdateCategoriesWorker>(4, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        val workManager = WorkManager.getInstance(this)

        workManager.enqueueUniquePeriodicWork(
            UpdateCategoriesWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            work
        )
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
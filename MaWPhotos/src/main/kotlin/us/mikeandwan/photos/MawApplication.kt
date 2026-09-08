package us.mikeandwan.photos

import android.app.Application
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import coil3.ImageLoader
import coil3.SingletonImageLoader
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import us.mikeandwan.photos.authorization.LegacyCredentialPurge
import us.mikeandwan.photos.workers.RandomPhotoWorker
import us.mikeandwan.photos.workers.UpdateCategoriesWorker

@HiltAndroidApp
class MawApplication :
    Application(),
    Configuration.Provider,
    SingletonImageLoader.Factory {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var imageLoader: ImageLoader

    @Inject
    lateinit var legacyCredentialPurge: LegacyCredentialPurge

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun newImageLoader(context: android.content.Context): ImageLoader = imageLoader

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())

            StrictMode.setThreadPolicy(
                ThreadPolicy
                    .Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork() // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build(),
            )

            StrictMode.setVmPolicy(
                VmPolicy
                    .Builder()
                    .detectLeakedSqlLiteObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build(),
            )
        }

        purgeLegacyCredentials()
        schedulePeriodicRefresh()
        scheduleWidgetUpdate()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration
            .Builder()
            .setWorkerFactory(workerFactory)
            .build()

    // the tokens the app wrote before it moved to an encrypted store are still sitting in
    // plaintext preferences on every device that has been through an older build, so each launch
    // gets one attempt to revoke and delete them until it succeeds
    private fun purgeLegacyCredentials() {
        applicationScope.launch {
            legacyCredentialPurge.run()
        }
    }

    private fun schedulePeriodicRefresh() {
        val constraints = Constraints
            .Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val work = PeriodicWorkRequestBuilder<UpdateCategoriesWorker>(4, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            UpdateCategoriesWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            work,
        )
    }

    private fun scheduleWidgetUpdate() {
        val constraints = Constraints
            .Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val work = PeriodicWorkRequestBuilder<RandomPhotoWorker>(1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            RandomPhotoWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            work,
        )
    }
}

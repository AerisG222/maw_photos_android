package us.mikeandwan.photos.services

import android.app.Application
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import us.mikeandwan.photos.MawApplication

class UploadJobScheduler(app: Application?, scheduler: JobScheduler?) :
    BaseJobScheduler(app, scheduler) {
    fun schedule(forceReschedule: Boolean) {
        var ji = getPendingJob(MawApplication.JOB_ID_UPLOAD_FILES)
        if (forceReschedule) {
            if (ji != null) {
                _scheduler.cancel(MawApplication.JOB_ID_UPLOAD_FILES)
            }
        } else {
            if (ji != null) {
                return
            }
        }
        val componentName = ComponentName(_app.applicationContext, UploadJobService::class.java)
        ji = JobInfo.Builder(MawApplication.JOB_ID_UPLOAD_FILES, componentName)
            .setPersisted(true)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
            .build()
        _scheduler.schedule(ji)
    }
}
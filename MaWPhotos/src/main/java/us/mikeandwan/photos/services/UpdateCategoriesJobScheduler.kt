package us.mikeandwan.photos.services

import android.app.Application
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import us.mikeandwan.photos.MawApplication

class UpdateCategoriesJobScheduler(app: Application?, scheduler: JobScheduler?) :
    BaseJobScheduler(app, scheduler) {
    fun schedule(forceReschedule: Boolean, milliseconds: Long) {
        var ji = getPendingJob(MawApplication.JOB_ID_UPDATE_CATEGORY)
        if (forceReschedule) {
            if (ji != null) {
                _scheduler.cancel(MawApplication.JOB_ID_UPDATE_CATEGORY)
            }
        } else {
            if (ji != null) {
                return
            }
        }
        val componentName =
            ComponentName(_app.applicationContext, UpdateCategoriesJobService::class.java)
        ji = JobInfo.Builder(MawApplication.JOB_ID_UPDATE_CATEGORY, componentName)
            .setPeriodic(milliseconds)
            .setPersisted(true)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .build()
        _scheduler.schedule(ji)
    }
}
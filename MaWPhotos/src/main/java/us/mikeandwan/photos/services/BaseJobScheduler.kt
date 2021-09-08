package us.mikeandwan.photos.services

import android.app.Application
import android.app.job.JobInfo
import android.app.job.JobScheduler

open class BaseJobScheduler(app: Application?, scheduler: JobScheduler?) {
    protected var _app: Application
    protected var _scheduler: JobScheduler
    protected fun getPendingJob(jobId: Int): JobInfo? {
        val list = _scheduler.allPendingJobs
        for (ji in list) {
            if (ji.id == jobId) {
                return ji
            }
        }
        return null
    }

    init {
        requireNotNull(app) { "app should not be null" }
        requireNotNull(scheduler) { "scheduler should not be null" }
        _app = app
        _scheduler = scheduler
    }
}
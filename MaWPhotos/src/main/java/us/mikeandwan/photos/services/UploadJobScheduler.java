package us.mikeandwan.photos.services;

import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;

import us.mikeandwan.photos.MawApplication;


public class UploadJobScheduler extends BaseJobScheduler {
    public UploadJobScheduler(Application app, JobScheduler scheduler) {
        super(app, scheduler);
    }


    public void schedule(boolean forceReschedule) {
        JobInfo ji = getPendingJob(MawApplication.JOB_ID_UPLOAD_FILES);

        if(forceReschedule) {
            if(ji != null) {
                _scheduler.cancel(MawApplication.JOB_ID_UPLOAD_FILES);
            }
        } else {
            if(ji != null) {
                return;
            }
        }

        ComponentName componentName = new ComponentName(_app.getApplicationContext(), UploadJobService.class);

        ji = new JobInfo.Builder(MawApplication.JOB_ID_UPLOAD_FILES, componentName)
            .setPersisted(true)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
            .build();

        _scheduler.schedule(ji);
    }
}

package us.mikeandwan.photos.services;

import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;

import us.mikeandwan.photos.MawApplication;

import static android.app.job.JobInfo.NETWORK_TYPE_ANY;


public class UpdateCategoriesJobScheduler extends BaseJobScheduler {
    public UpdateCategoriesJobScheduler(Application app, JobScheduler scheduler) {
        super(app, scheduler);
    }


    public void schedule(boolean forceReschedule, long milliseconds) {
        JobInfo ji = getPendingJob(MawApplication.JOB_ID_UPDATE_CATEGORY);

        if(forceReschedule) {
            if(ji != null) {
                _scheduler.cancel(MawApplication.JOB_ID_UPDATE_CATEGORY);
            }
        } else {
            if(ji != null) {
                return;
            }
        }

        ComponentName componentName = new ComponentName(_app.getApplicationContext(), UpdateCategoriesJobService.class);

        ji = new JobInfo.Builder(MawApplication.JOB_ID_UPDATE_CATEGORY, componentName)
            .setPeriodic(milliseconds)
            .setPersisted(true)
            .setRequiredNetworkType(NETWORK_TYPE_ANY)
            .build();

        _scheduler.schedule(ji);
    }
}

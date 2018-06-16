package us.mikeandwan.photos.services;

import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;

import java.util.List;

import us.mikeandwan.photos.MawApplication;

import static android.app.job.JobInfo.NETWORK_TYPE_ANY;


public class UpdateCategoriesJobScheduler {
    private Application _app;
    private JobScheduler _scheduler;

    public UpdateCategoriesJobScheduler(Application app, JobScheduler scheduler) {
        if(app == null) {
            throw new IllegalArgumentException("app should not be null");
        }

        if(scheduler == null) {
            throw new IllegalArgumentException("scheduler should not be null");
        }

        _app = app;
        _scheduler = scheduler;
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


    private JobInfo getPendingJob(int jobId) {
        List<JobInfo> list = _scheduler.getAllPendingJobs();

        for(JobInfo ji : list) {
            if(ji.getId() == jobId) {
                return ji;
            }
        }

        return null;
    }
}

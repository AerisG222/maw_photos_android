package us.mikeandwan.photos.services;

import android.app.Application;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;

import java.util.List;


public class BaseJobScheduler {
    protected Application _app;
    protected JobScheduler _scheduler;


    public BaseJobScheduler(Application app, JobScheduler scheduler) {
        if(app == null) {
            throw new IllegalArgumentException("app should not be null");
        }

        if(scheduler == null) {
            throw new IllegalArgumentException("scheduler should not be null");
        }

        _app = app;
        _scheduler = scheduler;
    }


    protected JobInfo getPendingJob(int jobId) {
        List<JobInfo> list = _scheduler.getAllPendingJobs();

        for(JobInfo ji : list) {
            if(ji.getId() == jobId) {
                return ji;
            }
        }

        return null;
    }
}

package us.mikeandwan.photos.services;

import android.app.job.JobScheduler;


public class UpdateCategoriesJobScheduler {
    private JobScheduler _scheduler;

    public UpdateCategoriesJobScheduler(JobScheduler scheduler) {
        if(scheduler == null) {
            throw new IllegalArgumentException("scheduler should not be null");
        }

        _scheduler = scheduler;
    }


    public void schedule() {

    }
}

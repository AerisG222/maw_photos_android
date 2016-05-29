package us.mikeandwan.photos.tasks;

import android.os.Handler;

import java.util.concurrent.ExecutionException;


public abstract class BackgroundTask<T> implements BackgroundTaskCallable {
    private BackgroundTaskPriority _priority = BackgroundTaskPriority.Normal;
    private Handler _handler = new Handler();


    public BackgroundTask() {
        this(BackgroundTaskPriority.Normal);
    }


    public BackgroundTask(BackgroundTaskPriority priority) {
        _priority = priority;
    }


    public BackgroundTaskPriority getPriority() {
        return _priority;
    }


    public void setPriority(BackgroundTaskPriority priority) {
        _priority = priority;
    }


    public Handler getHandler() {
        return _handler;
    }


    @Override
    public abstract T call() throws Exception;


    protected void postExecuteTask(T result) {
        // subclass can implement if desired
    }

    protected void handleException(ExecutionException ex) {
        // subclass can implement if desired
    }
}

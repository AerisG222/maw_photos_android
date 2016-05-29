package us.mikeandwan.photos.tasks;

import java.util.Comparator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class BackgroundTaskFuture<T> implements RunnableFuture<T> {
    private RunnableFuture<T> _src;
    private BackgroundTask _task;


    public BackgroundTaskFuture(RunnableFuture<T> other, BackgroundTask task) {
        _src = other;
        _task = task;
    }


    public BackgroundTask getBackgroundTask() {
        return _task;
    }


    public boolean cancel(boolean mayInterruptIfRunning) {
        return _src.cancel(mayInterruptIfRunning);
    }


    public boolean isCancelled() {
        return _src.isCancelled();
    }


    public boolean isDone() {
        return _src.isDone();
    }


    public T get() throws InterruptedException, ExecutionException {
        return _src.get();
    }


    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return _src.get();
    }


    public void run() {
        _src.run();
    }


    public static Comparator<Runnable> COMP = new Comparator<Runnable>() {
        public int compare(Runnable o1, Runnable o2) {
            if (o1 == null && o2 == null)
                return 0;
            else if (o1 == null)
                return -1;
            else if (o2 == null)
                return 1;
            else {
                int p1 = ((BackgroundTaskFuture<?>) o1).getBackgroundTask().getPriority().ordinal();
                int p2 = ((BackgroundTaskFuture<?>) o2).getBackgroundTask().getPriority().ordinal();

                return p1 > p2 ? 1 : (p1 == p2 ? 0 : -1);
            }
        }
    };
}

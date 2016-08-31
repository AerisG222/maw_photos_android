package us.mikeandwan.photos.tasks;

import android.util.Log;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import us.mikeandwan.photos.MawApplication;


public class BackgroundTaskExecutor {
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static volatile BackgroundTaskExecutor _instance = null;

    private static final ThreadPoolExecutor _threadPool = getPriorityExecutor(MAXIMUM_POOL_SIZE);


    private BackgroundTaskExecutor() {
        // hide constructor
    }


    // interesting discussion on the below technique:
    // http://en.wikipedia.org/wiki/Double-checked_locking
    public static BackgroundTaskExecutor getInstance() {
        BackgroundTaskExecutor result = _instance;

        if (result == null) {
            synchronized (BackgroundTaskExecutor.class) {
                result = _instance;

                if (result == null) {
                    _instance = result = new BackgroundTaskExecutor();
                    BackgroundTaskExecutor._threadPool.prestartAllCoreThreads();
                }
            }
        }

        return result;
    }


    public void enqueueTask(final BackgroundTask details) {
        _threadPool.submit(details);
    }


    public long getTaskCount() {
        return _threadPool.getTaskCount() - _threadPool.getCompletedTaskCount();
    }


    // http://stackoverflow.com/questions/3545623/how-to-implement-priorityblockingqueue-with-threadpoolexecutor-and-custom-tasks
    private static ThreadPoolExecutor getPriorityExecutor(int nThreads) {
        return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
            new PriorityBlockingQueue<>(10, BackgroundTaskFuture.COMP)) {

            protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
                RunnableFuture<T> newTaskFor = super.newTaskFor(callable);

                BackgroundTask<T> bt = (BackgroundTask<T>) callable;

                return new BackgroundTaskFuture<>(newTaskFor, bt);
            }

            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);

                if (t == null && r instanceof BackgroundTaskFuture<?>) {
                    try {
                        BackgroundTaskFuture<?> future = (BackgroundTaskFuture<?>) r;

                        BackgroundTask task = future.getBackgroundTask();

                        // create new wrapper runnable class that takes details of the task
                        task.getHandler().post(new BackgroundTaskCallbackRunnable(future));
                    } catch (Exception ex) {
                        Log.w(MawApplication.LOG_TAG, ex.getMessage(), ex);
                    }

                    /*
                    catch (CancellationException ce) {
                        t = ce;
                    } catch (ExecutionException ee) {
                        t = ee.getCause();
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt(); // ignore/reset
                    }
                    */
                }
                if (t != null) {
                    Log.w(MawApplication.LOG_TAG, t.getMessage(), t);
                }
            }
        };
    }


    private static class BackgroundTaskCallbackRunnable implements Runnable {
        private final BackgroundTask _task;
        private final BackgroundTaskFuture _future;


        public BackgroundTaskCallbackRunnable(BackgroundTaskFuture future) {
            _future = future;
            _task = future.getBackgroundTask();
        }


        @Override
        public void run() {
            try {
                _task.postExecuteTask(((Future<?>) _future).get());
            } catch (ExecutionException ex) {
                Log.w(MawApplication.LOG_TAG, ex.getMessage(), ex);
                _task.handleException(ex);
            } catch (Exception ex) {
                Log.e(MawApplication.LOG_TAG, ex.getMessage(), ex);
            }
        }
    }
}

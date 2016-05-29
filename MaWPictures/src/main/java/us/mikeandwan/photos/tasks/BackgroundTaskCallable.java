package us.mikeandwan.photos.tasks;

import java.util.concurrent.Callable;

public interface BackgroundTaskCallable<T> extends Callable<T> {
    BackgroundTaskPriority getPriority();
}

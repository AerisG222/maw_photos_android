package us.mikeandwan.photos.tasks;

import java.util.concurrent.Callable;

@SuppressWarnings("ALL")
interface BackgroundTaskCallable<T> extends Callable<T> {
    BackgroundTaskPriority getPriority();
}

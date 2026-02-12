package asynctaskmanagement.src.services;

import asynctaskmanagement.src.observer.ITaskLifecycleListener;

public interface ITaskExecutionService {
    public boolean enqueueTask(String taskId);

    public void markTaskFailed(String taskId);

    public void markTaskCompleted(String taskId);

    public boolean failTaskSilently(String taskId);

    public void addListener(ITaskLifecycleListener listener);
}

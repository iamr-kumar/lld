package asynctaskmanagement.src.services;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public interface ITaskManagementService {
    public String addTask(String name, Callable<Object> work);

    public CompletableFuture<Object> getTaskFuture(String taskId);

    public boolean addDependency(String taskId, String dependencyId);

    public boolean enqueueTask(String taskId);

    public void markTaskFailed(String taskId);

    public void markTaskCompleted(String taskId);
}

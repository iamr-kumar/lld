package asynctaskmanagement.src.services;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public interface ITaskSubmissionService {
    public String addTask(String name, Callable<Object> work);

    public CompletableFuture<Object> getTaskFuture(String taskId);

    public boolean addDependency(String taskId, String dependencyId);

}

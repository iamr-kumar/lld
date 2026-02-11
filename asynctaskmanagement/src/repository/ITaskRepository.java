package asynctaskmanagement.src.repository;

import asynctaskmanagement.src.models.Task;
import asynctaskmanagement.src.models.TaskNode;

public interface ITaskRepository {
    public Task getTaskById(String id);

    public TaskNode getTaskNodeById(String id);

    public void addTask(Task task);

    public void addDependencyToTask(String taskId, String dependencyId);
}

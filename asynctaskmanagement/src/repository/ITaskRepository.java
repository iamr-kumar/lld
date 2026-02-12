package asynctaskmanagement.src.repository;

import java.util.List;

import asynctaskmanagement.src.models.Task;
import asynctaskmanagement.src.models.TaskNode;

public interface ITaskRepository {
    public Task getTaskById(String id);

    public TaskNode getTaskNodeById(String id);

    public void addTask(Task task);

    public void addDependencyToTask(String taskId, String dependencyId);

    public List<String> getRootTaskIds();
}

package asynctaskmanagement.src.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import asynctaskmanagement.src.models.Task;
import asynctaskmanagement.src.models.TaskNode;

public class TaskRepository implements ITaskRepository {
    private final Map<String, Task> taskMap;
    private final Map<String, TaskNode> taskNodeMap;

    public TaskRepository() {
        this.taskMap = new ConcurrentHashMap<>();
        this.taskNodeMap = new ConcurrentHashMap<>();
    }

    @Override
    public Task getTaskById(String id) {
        return taskMap.get(id);
    }

    @Override
    public void addTask(Task task) {
        taskMap.put(task.getId(), task);
        taskNodeMap.put(task.getId(), new TaskNode(task));
    }

    @Override
    public TaskNode getTaskNodeById(String id) {
        return taskNodeMap.get(id);
    }

    @Override
    public void addDependencyToTask(String taskId, String dependencyId) {
        TaskNode taskNode = taskNodeMap.get(taskId);
        TaskNode dependencyNode = taskNodeMap.get(dependencyId);
        taskNode.addDependency(dependencyId);
        dependencyNode.addDependent(taskId);
    }
}

package asynctaskmanagement.src.services;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

import asynctaskmanagement.src.enums.TaskStatus;
import asynctaskmanagement.src.models.Task;
import asynctaskmanagement.src.models.TaskNode;
import asynctaskmanagement.src.repository.ITaskRepository;

public class TaskSubmissionService implements ITaskSubmissionService {
    private final ITaskRepository taskRepository;

    public TaskSubmissionService(ITaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public String addTask(String name, Callable<Object> work) {
        Task task = new Task(name, work);
        // Add task to repository and handle dependencies as needed
        this.taskRepository.addTask(task);
        return task.getId();
    }

    @Override
    public CompletableFuture<Object> getTaskFuture(String taskId) {
        Task task = this.taskRepository.getTaskById(taskId);
        if (task == null) {
            return null; // Or throw an exception if task not found
        }
        return task.getFuture();
    }

    @Override
    public boolean addDependency(String taskId, String dependencyId) {
        Task task = taskRepository.getTaskById(taskId);
        if (task == null || task.getStatus() != TaskStatus.NEW) {
            return false; // Task must exist and be in NEW status to add dependencies
        }
        // check if dependency exists
        Task dependencyTask = taskRepository.getTaskById(dependencyId);
        if (dependencyTask == null) {
            return false; // Dependency task must exist
        }

        // check for circular dependency
        if (hasCircularDependency(taskId, dependencyId)) {
            return false; // Adding this dependency would create a circular dependency
        }
        // check for duplicate dependency
        TaskNode taskNode = taskRepository.getTaskNodeById(taskId);
        if (taskNode.getDependencies().contains(dependencyId)) {
            return false; // Dependency already exists
        }
        taskRepository.addDependencyToTask(taskId, dependencyId);
        return true;
    }

    private boolean hasCircularDependency(String taskId, String dependencyId) {
        // Perform a DFS to check if taskId is reachable from dependencyId
        Map<String, Boolean> visited = new HashMap<>();
        return isReachable(dependencyId, taskId, visited);
    }

    private boolean isReachable(String startId, String targetId, Map<String, Boolean> visited) {
        if (startId.equals(targetId)) {
            return true;
        }
        TaskNode startNode = taskRepository.getTaskNodeById(startId);
        if (startNode == null) {
            return false;
        }
        visited.put(startId, true);
        for (String depId : startNode.getDependencies()) {
            if (!visited.getOrDefault(depId, false)) {
                if (isReachable(depId, targetId, visited)) {
                    return true;
                }
            }
        }
        return false;
    }
}

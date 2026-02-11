package asynctaskmanagement.src.services;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

import asynctaskmanagement.src.enums.TaskStatus;
import asynctaskmanagement.src.models.Task;
import asynctaskmanagement.src.models.TaskNode;
import asynctaskmanagement.src.repository.ITaskRepository;

public class TaskManagementService implements ITaskManagementService {
    private final BlockingQueue<String> readyQueue;
    private final ITaskRepository taskRepository;

    public TaskManagementService(BlockingQueue<String> readyQueue, ITaskRepository taskRepository) {
        this.readyQueue = readyQueue;
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

    @Override
    public boolean enqueueTask(String taskId) {
        Task task = this.taskRepository.getTaskById(taskId);
        if (task == null || task.getStatus() != TaskStatus.NEW) {
            return false; // Task must exist and be in NEW status to be enqueued
        }
        if (task.isInTerminalState()) {
            return false; // Task is already completed or failed, cannot be enqueued
        }
        TaskNode taskNode = this.taskRepository.getTaskNodeById(taskId);
        if (!taskNode.isReady()) {
            return false; // Task is not ready to be enqueued, it has unmet dependencies
        }
        if (!task.compareAndSetStatus(TaskStatus.NEW, TaskStatus.QUEUED)) {
            return false; // If we failed to update status, it means task was marked failed concurrently
        }
        this.readyQueue.offer(taskId);
        return true;
    }

    @Override
    public void markTaskCompleted(String taskId) {
        Task task = this.taskRepository.getTaskById(taskId);
        if (task == null || task.getStatus() != TaskStatus.RUNNING) {
            return; // Task must exist and be in RUNNING status to be marked completed
        }
        if (!task.compareAndSetStatus(TaskStatus.RUNNING, TaskStatus.COMPLETED)) {
            return; // If we failed to update status, it means task was marked failed concurrently
        }
        // Handle dependents and update their status if needed
        TaskNode taskNode = this.taskRepository.getTaskNodeById(taskId);
        for (String dependentId : taskNode.getDependents()) {
            TaskNode dependentNode = this.taskRepository.getTaskNodeById(dependentId);
            if (dependentNode.decrementAndCheckReady()) {
                enqueueTask(dependentId);
            }
        }
    }

    public void markTaskFailed(String taskId) {
        Task task = this.taskRepository.getTaskById(taskId);
        if (task == null) {
            return;
        }
        if (!task.compareAndSetStatus(TaskStatus.RUNNING, TaskStatus.FAILED)) {
            return; // If we failed to update status, it means task was marked completed
                    // concurrently
        }
        task.getFuture().completeExceptionally(new RuntimeException("Task failed")); // Complete future with exception
        // Mark dependents as failed
        TaskNode taskNode = this.taskRepository.getTaskNodeById(taskId);
        for (String dependentId : taskNode.getDependents()) {
            markTaskFailed(dependentId);
        }
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

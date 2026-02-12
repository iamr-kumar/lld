package asynctaskmanagement.src.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import asynctaskmanagement.src.enums.TaskStatus;
import asynctaskmanagement.src.models.Task;
import asynctaskmanagement.src.models.TaskNode;
import asynctaskmanagement.src.observer.ITaskLifecycleListener;
import asynctaskmanagement.src.repository.ITaskRepository;

public class TaskExecutionService implements ITaskExecutionService {
    private final BlockingQueue<String> readyQueue;
    private final ITaskRepository taskRepository;
    private final List<ITaskLifecycleListener> listeners;

    public TaskExecutionService(BlockingQueue<String> readyQueue, ITaskRepository taskRepository) {
        this.readyQueue = readyQueue;
        this.taskRepository = taskRepository;
        this.listeners = new ArrayList<>();
    }

    public void addListener(ITaskLifecycleListener listener) {
        this.listeners.add(listener);
    }

    private void notifyCompleted(String taskId) {
        for (ITaskLifecycleListener listener : listeners) {
            listener.onTaskCompleted(taskId);
        }
    }

    private void notifyFailed(String taskId) {
        for (ITaskLifecycleListener listener : listeners) {
            listener.onTaskFailed(taskId);
        }
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
        if (task == null) {
            return;
        }
        if (!task.compareAndSetStatus(TaskStatus.RUNNING, TaskStatus.COMPLETED)) {
            return; // If we failed to update status, it means task was marked failed concurrently
        }
        notifyCompleted(taskId);
    }

    @Override
    public void markTaskFailed(String taskId) {
        if (!failTaskSilently(taskId)) {
            return;
        }
        notifyFailed(taskId);
    }

    @Override
    public boolean failTaskSilently(String taskId) {
        Task task = this.taskRepository.getTaskById(taskId);
        if (task == null) {
            return false;
        }
        if (!task.setStatus(TaskStatus.FAILED)) {
            return false; // Already in terminal state
        }
        task.getFuture().completeExceptionally(new RuntimeException("Task " + taskId + " failed"));
        return true;
    }
}

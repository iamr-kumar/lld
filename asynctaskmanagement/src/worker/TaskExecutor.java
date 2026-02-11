package asynctaskmanagement.src.worker;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import asynctaskmanagement.src.enums.TaskStatus;
import asynctaskmanagement.src.models.Task;
import asynctaskmanagement.src.models.TaskNode;
import asynctaskmanagement.src.repository.ITaskRepository;
import asynctaskmanagement.src.services.ITaskManagementService;

public class TaskExecutor implements Runnable {
    private final BlockingQueue<String> readyQueue;
    private final ITaskRepository taskRepository;
    private final ITaskManagementService taskManagementService;
    private volatile boolean isRunning;

    public TaskExecutor(BlockingQueue<String> readyQueue, ITaskRepository taskRepository,
            ITaskManagementService taskManagementService) {
        this.readyQueue = readyQueue;
        this.taskRepository = taskRepository;
        this.taskManagementService = taskManagementService;
        this.isRunning = true;
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                String taskId = readyQueue.poll(500, TimeUnit.MILLISECONDS);
                if (taskId == null) {
                    continue; // No task available, check again
                }
                TaskNode taskNode = taskRepository.getTaskNodeById(taskId);
                Task task = taskNode.getTask();
                if (!task.compareAndSetStatus(TaskStatus.QUEUED, TaskStatus.RUNNING)) {
                    continue; // Task is not in QUEUED state, skip it
                }
                // Execute the task's work
                try {
                    Object result = task.getWork().call();
                    task.getFuture().complete(result);
                    // simulate work
                    Thread.sleep(5000);
                    taskManagementService.markTaskCompleted(task.getId());
                } catch (Exception e) {
                    task.getFuture().completeExceptionally(e);
                    taskManagementService.markTaskFailed(task.getId());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void shutdown() {
        isRunning = false;
    }
}

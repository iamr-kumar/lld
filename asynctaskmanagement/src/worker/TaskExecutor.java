package asynctaskmanagement.src.worker;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import asynctaskmanagement.src.enums.TaskStatus;
import asynctaskmanagement.src.models.Task;
import asynctaskmanagement.src.models.TaskNode;
import asynctaskmanagement.src.repository.ITaskRepository;
import asynctaskmanagement.src.services.ITaskExecutionService;

public class TaskExecutor implements Runnable {
    private final BlockingQueue<String> readyQueue;
    private final ITaskRepository taskRepository;
    private final ITaskExecutionService taskExecutionService;
    private volatile boolean isRunning;

    public TaskExecutor(BlockingQueue<String> readyQueue, ITaskRepository taskRepository,
            ITaskExecutionService taskExecutionService) {
        this.readyQueue = readyQueue;
        this.taskRepository = taskRepository;
        this.taskExecutionService = taskExecutionService;
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
                    taskExecutionService.markTaskCompleted(task.getId());
                    task.getFuture().complete(result);
                } catch (Exception e) {
                    taskExecutionService.markTaskFailed(task.getId());
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

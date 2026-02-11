package asynctaskmanagement.src;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import asynctaskmanagement.src.repository.ITaskRepository;
import asynctaskmanagement.src.repository.TaskRepository;
import asynctaskmanagement.src.services.ITaskManagementService;
import asynctaskmanagement.src.services.TaskManagementService;
import asynctaskmanagement.src.worker.TaskExecutor;

public class TaskManager {
    private static final int NUM_WORKERS = 5;

    public static void main(String[] args) {
        BlockingQueue<String> readyQueue = new ArrayBlockingQueue<>(100);
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        ITaskRepository taskRepository = new TaskRepository();
        ITaskManagementService taskManagementService = new TaskManagementService(readyQueue, taskRepository);

        Callable<Object> work1 = () -> {
            Thread.sleep(2000);
            return "Task 1 completed";
        };
        Callable<Object> work2 = () -> {
            Thread.sleep(3000);
            return "Task 2 completed";
        };
        Callable<Object> work3 = () -> {
            Thread.sleep(1000);
            return "Task 3 completed";
        };

        String taskId1 = taskManagementService.addTask("First Task", work1);
        String taskId2 = taskManagementService.addTask("Second Task", work2);
        String taskId3 = taskManagementService.addTask("Third Task", work3);

        var future1 = taskManagementService.getTaskFuture(taskId1);
        var future2 = taskManagementService.getTaskFuture(taskId2);
        var future3 = taskManagementService.getTaskFuture(taskId3);

        taskManagementService.addDependency(taskId2, taskId1);
        taskManagementService.addDependency(taskId3, taskId2);

        for (int i = 0; i < NUM_WORKERS; i++) {
            executorService.submit(new TaskExecutor(readyQueue, taskRepository, taskManagementService));
        }

        taskManagementService.enqueueTask(taskId1);
        future1.thenAccept(r -> System.out.println("Result of Task 1: " + r));
        future2.thenAccept(r -> System.out.println("Result of Task 2: " + r));
        future3.thenAccept(r -> System.out.println("Result of Task 3: " + r));

        CompletableFuture.allOf(future1, future2, future3).thenRun(() -> {
            System.out.println("All tasks completed");
            executorService.shutdown();
        });

        try {
            executorService.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }
}

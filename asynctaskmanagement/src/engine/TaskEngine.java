package asynctaskmanagement.src.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import asynctaskmanagement.src.observer.DependentListener;
import asynctaskmanagement.src.repository.ITaskRepository;
import asynctaskmanagement.src.repository.TaskRepository;
import asynctaskmanagement.src.services.ITaskExecutionService;
import asynctaskmanagement.src.services.ITaskSubmissionService;
import asynctaskmanagement.src.services.TaskExecutionService;
import asynctaskmanagement.src.services.TaskSubmissionService;
import asynctaskmanagement.src.worker.TaskExecutor;

public class TaskEngine {
    private static final int NUM_WORKERS = 4;
    private static final int READY_QUEUE_CAPACITY = 100;
    private final ITaskSubmissionService taskSubmissionService;
    private final ITaskExecutionService taskExecutionService;
    private final ExecutorService executorService;
    private final ITaskRepository taskRepository;
    private final BlockingQueue<String> readyQueue;
    private final List<TaskExecutor> workers;

    public TaskEngine() {
        this.readyQueue = new ArrayBlockingQueue<>(READY_QUEUE_CAPACITY);
        this.taskRepository = new TaskRepository();
        this.taskSubmissionService = new TaskSubmissionService(this.taskRepository);
        this.taskExecutionService = new TaskExecutionService(this.readyQueue, this.taskRepository);
        this.executorService = Executors.newFixedThreadPool(NUM_WORKERS);
        this.workers = new ArrayList<>();
        for (int i = 0; i < NUM_WORKERS; i++) {
            TaskExecutor worker = new TaskExecutor(readyQueue, taskRepository, taskExecutionService);
            this.workers.add(worker);
            this.executorService.submit(worker);
        }
        taskExecutionService.addListener(new DependentListener(taskRepository, taskExecutionService));
    }

    public String submitTask(String name, Callable<Object> work) {
        return this.taskSubmissionService.addTask(name, work);
    }

    public boolean addDependency(String taskId, String dependencyId) {
        return this.taskSubmissionService.addDependency(taskId, dependencyId);
    }

    public CompletableFuture<Object> getTaskFuture(String taskId) {
        return this.taskSubmissionService.getTaskFuture(taskId);
    }

    public boolean start() {
        // enqueue tasks with no dependencies
        List<String> readyTasks = this.taskRepository.getRootTaskIds();
        if (readyTasks.isEmpty()) {
            return false; // No tasks to execute -> all have dependencies -> circular dependency or no
                          // tasks at all
        }
        for (String taskId : readyTasks) {
            this.taskExecutionService.enqueueTask(taskId);
        }
        return true;
    }

    public boolean shutdown() {
        for (TaskExecutor worker : workers) {
            worker.shutdown();
        }
        this.executorService.shutdown();
        try {
            if (!this.executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                this.executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            this.executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        return true;
    }

}

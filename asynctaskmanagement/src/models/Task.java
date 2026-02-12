package asynctaskmanagement.src.models;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import asynctaskmanagement.src.enums.TaskStatus;

public class Task {
    private final String id;
    private final String name;
    private final Callable<Object> work;
    private final CompletableFuture<Object> future;
    private final AtomicReference<TaskStatus> status;
    private static final List<TaskStatus> TERMINAL_STATE = Arrays.asList(TaskStatus.COMPLETED, TaskStatus.FAILED,
            TaskStatus.CANCELLED);

    public Task(String name, Callable<Object> work) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.work = work;
        this.status = new AtomicReference<>(TaskStatus.NEW);
        this.future = new CompletableFuture<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Callable<Object> getWork() {
        return work;
    }

    public TaskStatus getStatus() {
        return status.get();
    }

    public boolean compareAndSetStatus(TaskStatus expected, TaskStatus newStatus) {
        return status.compareAndSet(expected, newStatus);
    }

    public boolean setStatus(TaskStatus newStatus) {
        while (true) {
            TaskStatus current = status.get();
            if (TERMINAL_STATE.contains(current)) {
                return false; // Cannot change status if already completed or failed
            }
            if (status.compareAndSet(current, newStatus)) {
                return true;
            }
        }
    }

    public boolean isInTerminalState() {
        return TERMINAL_STATE.contains(status.get());
    }

    public CompletableFuture<Object> getFuture() {
        return future;
    }
}

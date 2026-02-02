package task.state;

import task.TaskContext;
import task.TaskStatus;

public class CompletedState implements TaskState {

    @Override
    public void markAssigned(TaskContext context) {
        throw new IllegalStateException("Cannot assign a completed task.");
    }

    @Override
    public void markInProgress(TaskContext context) {
        throw new IllegalStateException("Cannot mark a completed task as in progress.");
    }

    @Override
    public void markCompleted(TaskContext context) {
        // Task is already completed, no action needed.
    }

    @Override
    public TaskStatus getStatus() {
        return TaskStatus.COMPLETED;
    }

}

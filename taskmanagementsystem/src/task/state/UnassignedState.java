package task.state;

import task.TaskContext;
import task.TaskStatus;

public class UnassignedState implements TaskState {

    @Override
    public void markAssigned(TaskContext context) {
        context.setStatus(new NotStartedState());
    }

    @Override
    public void markInProgress(TaskContext context) {
        throw new IllegalStateException("Cannot mark task as in progress when it is unassigned.");
    }

    @Override
    public void markCompleted(TaskContext context) {
        throw new IllegalStateException("Cannot mark task as completed when it is unassigned.");
    }

    @Override
    public TaskStatus getStatus() {
        return TaskStatus.UNASSIGNED;
    }
}
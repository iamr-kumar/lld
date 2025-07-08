package taskmanagementsystem.src.task.state;

import taskmanagementsystem.src.task.TaskContext;
import taskmanagementsystem.src.task.TaskStatus;

public class NotStartedState implements TaskState {

    @Override
    public void markAssigned(TaskContext context) {
        // Already assigned, no action needed
    }

    @Override
    public void markInProgress(TaskContext context) {
        context.setStatus(new InProgressState());
    }

    @Override
    public void markCompleted(TaskContext context) {
        throw new IllegalStateException("Cannot mark task as completed when it is not assigned.");
    }

    @Override
    public TaskStatus getStatus() {
        return TaskStatus.NOT_STARTED;
    }

}

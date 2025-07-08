package taskmanagementsystem.src.task.state;

import taskmanagementsystem.src.task.TaskContext;
import taskmanagementsystem.src.task.TaskStatus;

public class InProgressState implements TaskState {

    @Override
    public void markAssigned(TaskContext context) {
        throw new IllegalStateException("Cannot mark task as assigned when it is already in progress.");
    }

    @Override
    public void markInProgress(TaskContext context) {
        // Already in progress, no action needed
    }

    @Override
    public void markCompleted(TaskContext context) {
        context.setStatus(new CompletedState());
    }

    @Override
    public TaskStatus getStatus() {
        return TaskStatus.IN_PROGRESS;
    }

}

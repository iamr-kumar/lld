package taskmanagementsystem.src.task.state;

import taskmanagementsystem.src.task.TaskContext;
import taskmanagementsystem.src.task.TaskStatus;

public interface TaskState {
    void markAssigned(TaskContext context);

    void markInProgress(TaskContext context);

    void markCompleted(TaskContext context);

    TaskStatus getStatus();
}

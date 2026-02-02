package task.state;

import task.TaskContext;
import task.TaskStatus;

public interface TaskState {
    void markAssigned(TaskContext context);

    void markInProgress(TaskContext context);

    void markCompleted(TaskContext context);

    TaskStatus getStatus();
}

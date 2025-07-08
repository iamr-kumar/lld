package taskmanagementsystem.src.task;

import taskmanagementsystem.src.task.state.TaskState;
import taskmanagementsystem.src.task.state.UnassignedState;

public class TaskContext {
    private TaskState currentState;

    public TaskContext() {
        this.currentState = new UnassignedState();
    }

    public void setStatus(TaskState state) {
        this.currentState = state;
    }

    public TaskState getCurentState() {
        return currentState;
    }

    public TaskStatus getStatus() {
        return currentState.getStatus();
    }

    public void markAssigned() {
        currentState.markAssigned(this);
    }

    public void markInProgress() {
        currentState.markInProgress(this);
    }

    public void markCompleted() {
        currentState.markCompleted(this);
    }
}

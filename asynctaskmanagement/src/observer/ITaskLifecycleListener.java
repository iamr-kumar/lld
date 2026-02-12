package asynctaskmanagement.src.observer;

public interface ITaskLifecycleListener {
    void onTaskCompleted(String taskId);

    void onTaskFailed(String taskId);
}


import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import task.Task;
import task.TaskPriority;
import task.TaskStatus;
import task.User;

public class TaskManager {
    private static TaskManager instance;
    private final Map<String, Task> tasks;

    private TaskManager() {
        tasks = new ConcurrentHashMap<>();
    }

    public static synchronized TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }

    public String createTask(String title, String description, TaskPriority priority, Date dueDate) {
        Task task = new Task(title, description, priority, dueDate);
        tasks.put(task.getId(), task);
        System.out.println("Task created: " + task.getTitle() + " with ID: " + task.getId());
        return task.getId();
    }

    public Task getTaskById(String taskId) {
        return tasks.get(taskId);
    }

    public void assignUserToTask(String taskId, User user) {
        Task task = tasks.get(taskId);
        if (task != null) {
            task.setAssignedUser(user);
        }
        System.out.println("User " + user.getName() + " assigned to task: " + task.getTitle());
    }

    public void updateTaskStatus(String taskId, TaskStatus status) {
        Task task = tasks.get(taskId);
        if (task != null) {
            if (task.getStatus() == TaskStatus.IN_PROGRESS) {
                task.markInProgress();
            } else if (task.getStatus() == TaskStatus.COMPLETED) {
                task.markCompleted();
            } else {
                throw new IllegalStateException("Task must be in IN_PROGRESS or COMPLETED state to update status.");
            }
        } else {
            throw new IllegalArgumentException("Task with ID " + taskId + " does not exist.");
        }
    }

    public void updateTaskPriority(String taskId, TaskPriority priority) {
        Task task = tasks.get(taskId);
        if (task != null) {
            task.setPriority(priority);
        } else {
            throw new IllegalArgumentException("Task with ID " + taskId + " does not exist.");
        }
    }

    public List<Task> getAllTasks() {
        return tasks.values().stream()
                .collect(Collectors.toList());
    }

    public List<Task> getTasksByUser(User user) {
        return tasks.values().stream()
                .filter(task -> task.getAssignedUser() != null && task.getAssignedUser().equals(user))
                .collect(Collectors.toList());
    }

    public List<Task> getTasksByStatus(TaskStatus status) {
        return tasks.values().stream()
                .filter(task -> task.getStatus() == status)
                .collect(Collectors.toList());
    }

    public void deleteTask(String taskId) {
        tasks.remove(taskId);
    }

}

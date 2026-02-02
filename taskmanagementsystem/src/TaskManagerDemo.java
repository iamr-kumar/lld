
import java.util.Date;

import task.TaskPriority;
import task.TaskStatus;
import task.User;

public class TaskManagerDemo {
    public static void main(String[] args) {
        TaskManager taskManager = TaskManager.getInstance();

        // Create some users
        User user1 = new User(1, "Alice", "alice@gmail.com");
        User user2 = new User(2, "Bob", "bob@gmail.com ");

        // Add tasks to the task manager
        String taskId1 = taskManager.createTask("Task 1", "Description for Task 1", TaskPriority.HIGH, new Date());
        String taskId2 = taskManager.createTask("Task 2", "Description for Task 2", TaskPriority.MEDIUM, new Date());
        String taskId3 = taskManager.createTask("Task 3", "Description for Task 3", TaskPriority.LOW, new Date());

        // Assign users to tasks
        taskManager.assignUserToTask(taskId1, user1);
        taskManager.assignUserToTask(taskId2, user2);
        taskManager.assignUserToTask(taskId3, user1);

        // Update task status
        taskManager.updateTaskStatus(taskId1, TaskStatus.IN_PROGRESS);
        taskManager.updateTaskStatus(taskId2, TaskStatus.COMPLETED);
        taskManager.updateTaskStatus(taskId3, TaskStatus.NOT_STARTED);

        // Update task priority
        taskManager.updateTaskPriority(taskId1, TaskPriority.URGENT);
        taskManager.updateTaskPriority(taskId2, TaskPriority.LOW);

        // List tasks assigned to a user
        taskManager.getTasksByUser(user1).forEach(task -> {
            System.out
                    .println("Task assigned to " + user1.getName() + ": " + task.getTitle() + " - " + task.getStatus());
        });
    }
}

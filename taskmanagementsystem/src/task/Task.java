package task;

import java.util.Date;
import java.util.UUID;

public class Task {
    private String id;
    private String title;
    private String description;
    private TaskContext taskContext;
    private TaskPriority priority;
    private User assignedUser;
    private Date dueDate;

    public Task(String title, String description, TaskPriority priority, Date dueDate) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.assignedUser = null; // Initially, no user is assigned
        this.taskContext = new TaskContext();
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(User assignedUser) {
        this.assignedUser = assignedUser;
        taskContext.markAssigned();
    }

    public void markInProgress() {
        taskContext.markInProgress();
    }

    public void markCompleted() {
        taskContext.markCompleted();
    }

    public TaskStatus getStatus() {
        return taskContext.getStatus();
    }

}

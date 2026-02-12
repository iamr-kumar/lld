package asynctaskmanagement.src.observer;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import asynctaskmanagement.src.models.TaskNode;
import asynctaskmanagement.src.repository.ITaskRepository;
import asynctaskmanagement.src.services.ITaskExecutionService;

public class DependentListener implements ITaskLifecycleListener {
    private final ITaskRepository taskRepository;
    private final ITaskExecutionService taskExecutionService;

    public DependentListener(ITaskRepository taskRepository, ITaskExecutionService taskExecutionService) {
        this.taskRepository = taskRepository;
        this.taskExecutionService = taskExecutionService;
    }

    @Override
    public void onTaskCompleted(String taskId) {
        TaskNode taskNode = this.taskRepository.getTaskNodeById(taskId);
        for (String dependentId : taskNode.getDependents()) {
            TaskNode dependentNode = this.taskRepository.getTaskNodeById(dependentId);
            if (dependentNode.decrementAndCheckReady()) {
                taskExecutionService.enqueueTask(dependentId);
            }
        }
    }

    @Override
    public void onTaskFailed(String taskId) {
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        visited.add(taskId);
        queue.add(taskId);
        while (!queue.isEmpty()) {
            String currentId = queue.poll();
            TaskNode currentNode = this.taskRepository.getTaskNodeById(currentId);
            for (String dependentId : currentNode.getDependents()) {
                if (visited.add(dependentId)) {
                    // Use failTaskSilently to avoid re-triggering this listener
                    taskExecutionService.failTaskSilently(dependentId);
                    queue.add(dependentId);
                }
            }
        }
    }
}
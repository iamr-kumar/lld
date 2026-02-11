package asynctaskmanagement.src.models;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskNode {
    private final Task task;
    private final Set<String> dependencies;
    private final Set<String> dependents;
    private final AtomicInteger pendingDependencies;

    public TaskNode(Task task) {
        this.task = task;
        this.dependencies = ConcurrentHashMap.newKeySet();
        this.dependents = ConcurrentHashMap.newKeySet();
        this.pendingDependencies = new AtomicInteger(dependencies.size());
    }

    public Task getTask() {
        return task;
    }

    public Set<String> getDependencies() {
        return dependencies;
    }

    public Set<String> getDependents() {
        return dependents;
    }

    public void addDependency(String taskId) {
        dependencies.add(taskId);
        pendingDependencies.incrementAndGet();
    }

    public void addDependent(String taskId) {
        dependents.add(taskId);
    }

    /**
     * Called when one dependency completes.
     * Returns true if all dependencies are now satisfied, false otherwise.
     */
    public boolean decrementAndCheckReady() {
        return pendingDependencies.decrementAndGet() == 0;
    }

    public boolean isReady() {
        return pendingDependencies.get() == 0;
    }
}

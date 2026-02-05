package filesystem.src.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class Directory extends FileSystemEntity {
    private final Map<String, FileSystemEntity> children;
    private final ReentrantLock lock;

    private AtomicLong cachedSize;

    public Directory(String name, Directory parent) {
        super(name, parent);
        this.children = new ConcurrentHashMap<>();
        this.cachedSize = new AtomicLong(0);
        this.lock = new ReentrantLock();
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    @Override
    public long getSize() {
        return cachedSize.get();
    }

    @Override
    public void delete() {
        if (parent != null) {
            parent.removeChild(this.name);
        }
        lock.lock();
        try {
            List<FileSystemEntity> childEntities = new ArrayList<>(children.values());
            for (FileSystemEntity child : childEntities) {
                child.delete();
            }
            children.clear();
        } finally {
            lock.unlock();
        }

    }

    public void addChild(FileSystemEntity child) {
        FileSystemEntity previous = children.putIfAbsent(child.getName(), child);
        if (previous == null) {
            updateSize(child.getSize());
        }
    }

    public Directory getOrCreateChildDirectory(String name, Directory parent) {
        lock.lock();
        try {
            FileSystemEntity existing = children.get(name);
            if (existing != null) {
                if (existing.isDirectory()) {
                    return (Directory) existing;
                } else {
                    return null; // name conflict with a file, cannot create directory
                }
            }
            Directory newDir = new Directory(name, parent);
            children.put(name, newDir);
            return newDir;
        } finally {
            lock.unlock();
        }
    }

    public boolean createFileIfAbsent(String name) {
        lock.lock();
        try {
            FileSystemEntity existing = children.get(name);
            if (existing != null) {
                return false; // file or directory with same name already exists
            }
            File newFile = new File(name, this);
            children.put(name, newFile);
            updateSize(newFile.getSize());
            return true;
        } finally {
            lock.unlock();
        }
    }

    public FileSystemEntity getChild(String name) {
        return children.get(name);
    }

    public void removeChild(String name) {
        lock.lock();
        try {
            FileSystemEntity removed = children.remove(name);
            if (removed != null) {
                updateSize(-removed.getSize());
            }
        } finally {
            lock.unlock();
        }

    }

    public void updateSize(long delta) {
        cachedSize.addAndGet(delta);
        if (parent != null) {
            parent.updateSize(delta);
        }
    }

    public List<FileSystemEntity> getChildren() {
        return new ArrayList<>(children.values());
    }

    public boolean containsChild(String name) {
        return children.containsKey(name);
    }
}

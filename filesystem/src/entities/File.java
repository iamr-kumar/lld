package filesystem.src.entities;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class File extends FileSystemEntity {
    private AtomicLong size;
    private String content;
    private final ReentrantReadWriteLock rwLock;

    public File(String name, Directory parent) {
        super(name, parent);
        this.size = new AtomicLong(0);
        this.content = "";
        rwLock = new ReentrantReadWriteLock();
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public long getSize() {
        return size.get();
    }

    @Override
    public void delete() {
        if (parent != null) {
            parent.removeChild(this.name);
        }
    }

    public void setContent(String content) {
        rwLock.writeLock().lock();
        try {
            this.content = content;
            long newSize = content.length();
            long sizeChange = newSize - this.size.get();
            this.size.set(newSize);
            if (parent != null) {
                parent.updateSize(sizeChange);
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public String getContent() {
        rwLock.readLock().lock();
        try {
            return content;
        } finally {
            rwLock.readLock().unlock();
        }
    }
}
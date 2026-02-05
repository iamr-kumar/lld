package filesystem.src.entities;

public abstract class FileSystemEntity {
    protected String name;
    protected Directory parent;

    public FileSystemEntity(String name, Directory parent) {
        this.name = name;
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public Directory getParent() {
        return parent;
    }

    public String getPath() {
        Directory localParent = this.parent;
        if (localParent == null)
            return "/";
        String parentPath = localParent.getPath();
        return parentPath.equals("/") ? "/" + name : parentPath + "/" + name;
    }

    public abstract boolean isDirectory();

    public abstract long getSize();

    public abstract void delete();
}

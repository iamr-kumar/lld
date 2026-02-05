package filesystem.src.services;

import java.util.ArrayList;
import java.util.List;

import filesystem.src.entities.Directory;
import filesystem.src.strategy.AbsolutePathResolutionStrategy;
import filesystem.src.strategy.IPathResolutionStrategy;
import filesystem.src.strategy.RelativePathStrategy;
import filesystem.src.strategy.WildcardResolutionStrategy;

public class FileService implements IFileService {
    private final Directory root;
    private Directory currentDirectory;

    private final List<IPathResolutionStrategy> pathResolutionStrategies;

    public FileService(Directory root) {
        this.root = root;
        this.currentDirectory = root;
        this.pathResolutionStrategies = new ArrayList<>();
        this.pathResolutionStrategies.add(new WildcardResolutionStrategy());
        this.pathResolutionStrategies.add(new AbsolutePathResolutionStrategy());
        this.pathResolutionStrategies.add(new RelativePathStrategy());
    }

    public boolean mkdir(String path) {
        if (path == null || path.isEmpty()) {
            System.err.println("mkdir: Invalid path");
            return false;
        }
        String[] segments;
        Directory current;
        if (path.startsWith("/")) {
            if (path.equals("/")) {
                System.err.println("mkdir: Cannot create root directory");
                return false;
            }
            segments = path.substring(1).split("/");
            current = root;
        } else {
            segments = path.split("/");
            current = currentDirectory;
        }

        for (String segment : segments) {
            if (segment.isEmpty() || segment.equals(".")) {
                continue;
            }
            if (segment.equals("..")) {
                if (current.getParent() != null) {
                    current = current.getParent();
                }
                continue;
            }

            Directory next = current.getOrCreateChildDirectory(segment, current);
            if (next == null) {
                System.err.println("mkdir: Cannot create directory '" + segment
                        + "': A file with the same name already exists");
                return false;
            }
            current = next;
        }
        return true;

    }

    @Override
    public String pwd() {
        return currentDirectory.getPath();
    }

    @Override
    public boolean cd(String path) {
        if (path == null || path.isEmpty()) {
            System.err.println("cd: Invalid path");
            return false;
        }
        IPathResolutionStrategy strategy = getStrategy(path);
        List<Directory> resolvedPath = strategy.resolvePath(path, root, currentDirectory);
        if (resolvedPath.isEmpty()) {
            System.err.println("cd: No such directory: " + path);
            return false;
        }
        // in case of wildcard, we take the first match
        currentDirectory = resolvedPath.get(0);
        return true;
    }

    public boolean touch(String path) {
        if (path == null || path.isEmpty()) {
            System.err.println("touch: Invalid path");
            return false;
        }
        String parentPath;
        String fileName;

        int lastSlash = path.lastIndexOf('/');
        if (lastSlash == -1) {
            parentPath = ".";
            fileName = path;
        } else if (lastSlash == 0) {
            parentPath = "/";
            fileName = path.substring(1);
        } else {
            parentPath = path.substring(0, lastSlash);
            fileName = path.substring(lastSlash + 1);
        }

        Directory parent;
        if (parentPath == ".") {
            parent = currentDirectory;
        } else if (parentPath == "/") {
            parent = root;
        } else {
            IPathResolutionStrategy strategy = getStrategy(parentPath);
            List<Directory> resolved = strategy.resolvePath(parentPath, root, currentDirectory);
            if (resolved.isEmpty()) {
                System.err.println("touch: No such directory: " + parentPath);
                return false;
            }
            parent = resolved.get(0);
        }

        return parent.createFileIfAbsent(fileName);
    }

    public Directory getCurrentDirectory() {
        return currentDirectory;
    }

    public Directory getRootDirectory() {
        return root;
    }

    private IPathResolutionStrategy getStrategy(String path) {
        for (IPathResolutionStrategy strategy : pathResolutionStrategies) {
            if (strategy.supports(path)) {
                return strategy;
            }
        }
        return new WildcardResolutionStrategy();
    }

}

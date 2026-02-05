package filesystem.src.strategy;

import java.util.List;

import filesystem.src.entities.Directory;

public interface IPathResolutionStrategy {
    public List<Directory> resolvePath(String path, Directory root, Directory current);

    public boolean supports(String path);
}

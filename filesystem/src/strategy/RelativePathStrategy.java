package filesystem.src.strategy;

import java.util.List;

import filesystem.src.entities.Directory;

public class RelativePathStrategy extends AbstractResolutionStrategy {
    @Override
    public List<Directory> resolvePath(String path, Directory root, Directory current) {
        String[] segments = path.split("/");
        return resolveSegments(segments, current);
    }

    @Override
    public boolean supports(String path) {
        return !path.startsWith("/") && !path.contains("*") && !path.contains("?");
    }
}

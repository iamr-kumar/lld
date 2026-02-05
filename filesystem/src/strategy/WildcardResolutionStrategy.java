package filesystem.src.strategy;

import java.util.List;

import filesystem.src.entities.Directory;

public class WildcardResolutionStrategy extends AbstractResolutionStrategy {
    @Override
    public List<Directory> resolvePath(String path, Directory root, Directory current) {
        if (path.startsWith("/")) {
            String pathWithoutRoot = path.substring(1);
            String[] segments = pathWithoutRoot.split("/");
            return resolveSegments(segments, root);
        } else {
            String[] segments = path.split("/");
            return resolveSegments(segments, current);
        }
    }

    @Override
    public boolean supports(String path) {
        return path.contains("*") || path.contains("?");
    }
}

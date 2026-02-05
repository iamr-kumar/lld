package filesystem.src.strategy;

import java.util.List;

import filesystem.src.entities.Directory;

public class AbsolutePathResolutionStrategy extends AbstractResolutionStrategy {

    @Override
    public List<Directory> resolvePath(String path, Directory root, Directory current) {
        if (path.equals("/")) {
            return List.of(root);
        }
        String pathWithoutRoot = path.substring(1);
        String[] segments = pathWithoutRoot.split("/");

        return resolveSegments(segments, root);
    }

    @Override
    public boolean supports(String path) {
        return path.startsWith("/");
    }

}

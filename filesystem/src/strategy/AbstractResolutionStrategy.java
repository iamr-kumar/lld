package filesystem.src.strategy;

import java.util.ArrayList;
import java.util.List;

import filesystem.src.entities.Directory;
import filesystem.src.entities.FileSystemEntity;

public abstract class AbstractResolutionStrategy
        implements IPathResolutionStrategy {
    protected List<Directory> resolveSegments(String[] segments, Directory startDir) {
        List<Directory> current = new ArrayList<>();
        current.add(startDir);

        for (String segment : segments) {
            if (segment.isEmpty() || segment.equals(".")) {
                continue;
            }
            List<Directory> nextDirs = new ArrayList<>();
            for (Directory dir : current) {
                if (segment.equals("..")) {
                    Directory parent = dir.getParent();
                    if (parent != null) {
                        nextDirs.add(parent);
                    } else {
                        nextDirs.add(dir); // stay in root
                    }
                } else if (isWildcard(segment)) {
                    nextDirs.addAll(getAllMatchingChildren(dir, segment));
                } else {
                    // exact match
                    FileSystemEntity child = dir.getChild(segment);
                    if (child != null && child.isDirectory()) {
                        nextDirs.add((Directory) child);
                    }
                }
            }
            if (nextDirs.isEmpty()) {
                return new ArrayList<>(); // no matches, stop processing
            }
            current = nextDirs;
        }
        return current;
    }

    protected boolean isWildcard(String segment) {
        return segment.contains("*") || segment.contains("?");
    }

    protected List<Directory> getAllMatchingChildren(Directory dir, String pattern) {
        List<Directory> matches = new ArrayList<>();
        String regex = pattern.replace("?", ".").replace("*", ".*");
        for (FileSystemEntity child : dir.getChildren()) {
            if (child.isDirectory() && child.getName().matches(regex)) {
                matches.add((Directory) child);
            }
        }
        return matches;
    }
}

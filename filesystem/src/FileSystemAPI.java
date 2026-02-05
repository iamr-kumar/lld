package filesystem.src;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import filesystem.src.entities.Directory;
import filesystem.src.entities.File;
import filesystem.src.entities.FileSystemEntity;
import filesystem.src.services.FileService;

/**
 * FileSystem API Test Suite
 * Tests for mkdir, cd, pwd, touch, file operations, and concurrency.
 */
public class FileSystemAPI {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║            FILE SYSTEM API - TEST SUITE                      ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝\n");

        // Basic Tests
        testMkdirBasic();
        testPwd();
        testCdBasic();
        testTouchBasic();
        testFileContent();

        // Edge Cases
        testMkdirEdgeCases();
        testCdEdgeCases();
        testTouchEdgeCases();
        testPathNavigation();
        testDeleteOperations();
        testSizeCalculation();

        // Concurrency Tests
        testConcurrentMkdir();
        testConcurrentTouch();
        testConcurrentFileWrite();
        testConcurrentMixedOperations();

        printTestSummary();
    }

    // ═══════════════════════════════════════════════════════════════════
    // TEST 1: Basic mkdir Operations
    // ═══════════════════════════════════════════════════════════════════
    private static void testMkdirBasic() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("TEST 1: Basic mkdir Operations");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        FileService fs = createFileService();

        // Create single directory
        boolean result1 = fs.mkdir("documents");
        assertTest("mkdir 'documents' should succeed", result1);

        // Create nested directories
        boolean result2 = fs.mkdir("documents/work/projects");
        assertTest("mkdir 'documents/work/projects' should succeed", result2);

        // Create directory with absolute path
        boolean result3 = fs.mkdir("/home/user/downloads");
        assertTest("mkdir '/home/user/downloads' should succeed", result3);

        // Verify directories exist by cd'ing into them
        boolean canCd = fs.cd("documents/work/projects");
        assertTest("Should be able to cd into created directories", canCd);

        System.out.println("  Current directory: " + fs.pwd());
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════
    // TEST 2: pwd Operation
    // ═══════════════════════════════════════════════════════════════════
    private static void testPwd() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("TEST 2: pwd Operation");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        FileService fs = createFileService();

        // pwd at root
        String rootPath = fs.pwd();
        assertTest("pwd at root should return '/'", rootPath.equals("/"));

        // Create and navigate
        fs.mkdir("level1/level2/level3");
        fs.cd("level1/level2");
        String currentPath = fs.pwd();
        assertTest("pwd should return '/level1/level2'", currentPath.equals("/level1/level2"));

        fs.cd("level3");
        String deepPath = fs.pwd();
        assertTest("pwd should return '/level1/level2/level3'", deepPath.equals("/level1/level2/level3"));

        System.out.println("  Root path: " + rootPath);
        System.out.println("  After cd level1/level2: " + currentPath);
        System.out.println("  After cd level3: " + deepPath);
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════
    // TEST 3: Basic cd Operations
    // ═══════════════════════════════════════════════════════════════════
    private static void testCdBasic() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("TEST 3: Basic cd Operations");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        FileService fs = createFileService();
        fs.mkdir("dir1/dir2/dir3");
        fs.mkdir("other");

        // cd into relative path
        boolean cd1 = fs.cd("dir1");
        assertTest("cd 'dir1' should succeed", cd1);
        assertTest("pwd should be '/dir1'", fs.pwd().equals("/dir1"));

        // cd into nested directory
        boolean cd2 = fs.cd("dir2/dir3");
        assertTest("cd 'dir2/dir3' should succeed", cd2);
        assertTest("pwd should be '/dir1/dir2/dir3'", fs.pwd().equals("/dir1/dir2/dir3"));

        // cd with absolute path
        boolean cd3 = fs.cd("/other");
        assertTest("cd '/other' should succeed", cd3);
        assertTest("pwd should be '/other'", fs.pwd().equals("/other"));

        // cd to root
        boolean cd4 = fs.cd("/");
        assertTest("cd '/' should succeed", cd4);
        assertTest("pwd should be '/'", fs.pwd().equals("/"));

        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════
    // TEST 4: Basic touch Operations
    // ═══════════════════════════════════════════════════════════════════
    private static void testTouchBasic() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("TEST 4: Basic touch Operations");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        FileService fs = createFileService();
        Directory root = getRootDirectory(fs);

        // Create file in current directory
        boolean touch1 = fs.touch("file1.txt");
        assertTest("touch 'file1.txt' should succeed", touch1);
        assertTest("file1.txt should exist in root", root.containsChild("file1.txt"));

        // Create file in subdirectory
        fs.mkdir("docs");
        boolean touch2 = fs.touch("docs/readme.md");
        assertTest("touch 'docs/readme.md' should succeed", touch2);

        // Create file with absolute path
        fs.mkdir("/tmp");
        boolean touch3 = fs.touch("/tmp/temp.log");
        assertTest("touch '/tmp/temp.log' should succeed", touch3);

        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════
    // TEST 5: File Content Operations
    // ═══════════════════════════════════════════════════════════════════
    private static void testFileContent() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("TEST 5: File Content Operations");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        Directory root = new Directory("", null);
        File file = new File("test.txt", root);
        root.addChild(file);

        // Initial state
        assertTest("New file should have empty content", file.getContent().isEmpty());
        assertTest("New file should have size 0", file.getSize() == 0);

        // Write content
        String content = "Hello, World!";
        file.setContent(content);
        assertTest("File content should match", file.getContent().equals(content));
        assertTest("File size should match content length", file.getSize() == content.length());

        // Overwrite content
        String newContent = "New content";
        file.setContent(newContent);
        assertTest("File content should be updated", file.getContent().equals(newContent));
        assertTest("File size should be updated", file.getSize() == newContent.length());

        System.out.println("  Content: " + file.getContent());
        System.out.println("  Size: " + file.getSize());
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════
    // TEST 6: mkdir Edge Cases
    // ═══════════════════════════════════════════════════════════════════
    private static void testMkdirEdgeCases() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("TEST 6: mkdir Edge Cases");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        FileService fs = createFileService();
        Directory root = getRootDirectory(fs);

        // Empty path
        boolean empty = fs.mkdir("");
        assertTest("mkdir '' should fail", !empty);

        // Null path
        boolean nullPath = fs.mkdir(null);
        assertTest("mkdir null should fail", !nullPath);

        // Root directory
        boolean rootDir = fs.mkdir("/");
        assertTest("mkdir '/' should fail (root already exists)", !rootDir);

        // Duplicate directory (should succeed - idempotent)
        fs.mkdir("dupdir");
        boolean dup = fs.mkdir("dupdir");
        assertTest("mkdir existing directory should succeed (idempotent)", dup);

        // Create directory where file exists with same name
        fs.touch("existingfile");
        boolean conflict = fs.mkdir("existingfile");
        assertTest("mkdir should fail if file exists with same name", !conflict);

        // Deeply nested path
        boolean deep = fs.mkdir("a/b/c/d/e/f/g/h/i/j");
        assertTest("mkdir deeply nested path should succeed", deep);
        boolean canCdDeep = fs.cd("/a/b/c/d/e/f/g/h/i/j");
        assertTest("Should be able to cd to deep path", canCdDeep);

        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════
    // TEST 7: cd Edge Cases
    // ═══════════════════════════════════════════════════════════════════
    private static void testCdEdgeCases() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("TEST 7: cd Edge Cases");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        FileService fs = createFileService();
        fs.mkdir("testdir/subdir");

        // cd to non-existent directory
        boolean nonExistent = fs.cd("nonexistent");
        assertTest("cd to non-existent directory should fail", !nonExistent);
        assertTest("pwd should still be at root after failed cd", fs.pwd().equals("/"));

        // cd with empty string
        boolean emptyPath = fs.cd("");
        assertTest("cd '' should fail", !emptyPath);

        // cd with null
        boolean nullPath = fs.cd(null);
        assertTest("cd null should fail", !nullPath);

        // cd with parent navigation (..)
        fs.cd("testdir/subdir");
        boolean parent = fs.cd("..");
        assertTest("cd '..' should succeed", parent);
        assertTest("pwd should be '/testdir' after cd ..", fs.pwd().equals("/testdir"));

        // cd .. from root
        fs.cd("/");
        boolean rootParent = fs.cd("..");
        assertTest("cd '..' from root should stay at root", fs.pwd().equals("/"));

        // cd with dot (.)
        fs.cd("/testdir");
        boolean dot = fs.cd(".");
        assertTest("cd '.' should succeed", dot);
        assertTest("pwd should remain '/testdir' after cd .", fs.pwd().equals("/testdir"));

        // cd to file (should fail)
        fs.touch("/afile.txt");
        fs.cd("/");
        boolean cdToFile = fs.cd("afile.txt");
        assertTest("cd to a file should fail", !cdToFile);

        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════
    // TEST 8: touch Edge Cases
    // ═══════════════════════════════════════════════════════════════════
    private static void testTouchEdgeCases() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("TEST 8: touch Edge Cases");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        FileService fs = createFileService();

        // Empty path
        boolean empty = fs.touch("");
        assertTest("touch '' should fail", !empty);

        // Null path
        boolean nullPath = fs.touch(null);
        assertTest("touch null should fail", !nullPath);

        // Touch existing file (should fail - file exists)
        fs.touch("existing.txt");
        boolean duplicate = fs.touch("existing.txt");
        assertTest("touch existing file should fail", !duplicate);

        // Touch file where directory exists with same name
        fs.mkdir("dirname");
        boolean conflict = fs.touch("dirname");
        assertTest("touch should fail if directory exists with same name", !conflict);

        // Touch in non-existent directory
        boolean noParent = fs.touch("nonexistent/file.txt");
        assertTest("touch in non-existent directory should fail", !noParent);

        // Touch with special characters in name
        boolean special = fs.touch("file-with_special.chars.txt");
        assertTest("touch with special characters should succeed", special);

        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════
    // TEST 9: Path Navigation with .. and .
    // ═══════════════════════════════════════════════════════════════════
    private static void testPathNavigation() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("TEST 9: Path Navigation with .. and .");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        FileService fs = createFileService();

        // Create complex structure
        fs.mkdir("home/user/documents");
        fs.mkdir("home/user/downloads");
        fs.mkdir("var/log");

        // Navigate with ..
        fs.cd("home/user/documents");
        assertTest("Starting at /home/user/documents", fs.pwd().equals("/home/user/documents"));

        fs.cd("../downloads");
        assertTest("cd '../downloads' should work", fs.pwd().equals("/home/user/downloads"));

        // Create directory using .. in path
        boolean mkdirWithParent = fs.mkdir("../newdir");
        assertTest("mkdir '../newdir' should succeed", mkdirWithParent);

        // Create file using .. in path
        fs.cd("/home/user/documents");
        boolean touchWithParent = fs.touch("../sibling.txt");
        assertTest("touch '../sibling.txt' should succeed", touchWithParent);

        // Mixed path with . and ..
        fs.cd("/home/user");
        boolean mixedCd = fs.cd("./documents/../downloads");
        assertTest("cd './documents/../downloads' should work", fs.pwd().equals("/home/user/downloads"));

        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════
    // TEST 10: Delete Operations
    // ═══════════════════════════════════════════════════════════════════
    private static void testDeleteOperations() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("TEST 10: Delete Operations");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        Directory root = new Directory("", null);

        // Create file and delete
        File file = new File("deleteme.txt", root);
        root.addChild(file);
        assertTest("File should exist before delete", root.containsChild("deleteme.txt"));

        file.delete();
        assertTest("File should not exist after delete", !root.containsChild("deleteme.txt"));

        // Create directory with children and delete
        Directory parent = new Directory("parent", root);
        root.addChild(parent);
        Directory child1 = new Directory("child1", parent);
        Directory child2 = new Directory("child2", parent);
        parent.addChild(child1);
        parent.addChild(child2);
        File childFile = new File("childfile.txt", child1);
        child1.addChild(childFile);

        assertTest("Parent directory should have children", parent.getChildren().size() == 2);

        parent.delete();
        assertTest("Parent should not exist after delete", !root.containsChild("parent"));

        // Verify root is clean
        List<FileSystemEntity> remaining = root.getChildren();
        assertTest("Root should have no children after deletions", remaining.isEmpty());

        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════
    // TEST 11: Size Calculation
    // ═══════════════════════════════════════════════════════════════════
    private static void testSizeCalculation() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("TEST 11: Size Calculation");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        Directory root = new Directory("", null);

        // Empty directory should have size 0
        assertTest("Empty directory should have size 0", root.getSize() == 0);

        // Add file with content
        File file1 = new File("file1.txt", root);
        root.addChild(file1);
        file1.setContent("Hello"); // 5 bytes
        assertTest("Root size should be 5 after adding file", root.getSize() == 5);

        // Add another file
        File file2 = new File("file2.txt", root);
        root.addChild(file2);
        file2.setContent("World!"); // 6 bytes
        assertTest("Root size should be 11 after adding second file", root.getSize() == 11);

        // Nested directory
        Directory subdir = new Directory("subdir", root);
        root.addChild(subdir);
        File nestedFile = new File("nested.txt", subdir);
        subdir.addChild(nestedFile);
        nestedFile.setContent("Nested content here!"); // 20 bytes
        assertTest("Root size should include nested file", root.getSize() == 31);
        assertTest("Subdir size should be 20", subdir.getSize() == 20);

        // Update file content (smaller)
        file1.setContent("Hi"); // 2 bytes (was 5)
        assertTest("Root size should be 28 after reducing file1 content", root.getSize() == 28);

        // Delete file
        file2.delete();
        assertTest("Root size should be 22 after deleting file2", root.getSize() == 22);

        System.out.println("  Final root size: " + root.getSize());
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════
    // TEST 12: Concurrent mkdir Operations
    // ═══════════════════════════════════════════════════════════════════
    private static void testConcurrentMkdir() throws InterruptedException {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("TEST 12: Concurrent mkdir Operations");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        final int NUM_THREADS = 20;
        final Directory root = new Directory("", null);
        final FileService fs = new FileService(root);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(NUM_THREADS);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errors = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        // All threads try to create directories simultaneously
        for (int i = 0; i < NUM_THREADS; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    // Each thread creates its own directory
                    boolean result = fs.mkdir("dir" + threadId);
                    if (result) {
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    errors.incrementAndGet();
                    e.printStackTrace();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        doneLatch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        assertTest("No exceptions during concurrent mkdir", errors.get() == 0);
        assertTest("All mkdir operations should succeed", successCount.get() == NUM_THREADS);
        assertTest("Root should have " + NUM_THREADS + " children", root.getChildren().size() == NUM_THREADS);

        System.out.println("  Threads: " + NUM_THREADS);
        System.out.println("  Successful mkdir: " + successCount.get());
        System.out.println("  Directories created: " + root.getChildren().size());
        System.out.println("  Errors: " + errors.get());
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════
    // TEST 13: Concurrent touch - Same Directory Race
    // ═══════════════════════════════════════════════════════════════════
    private static void testConcurrentTouch() throws InterruptedException {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("TEST 13: Concurrent touch - Same Directory Race");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        final int NUM_THREADS = 20;
        final Directory root = new Directory("", null);
        final FileService fs = new FileService(root);

        // Create a shared directory
        fs.mkdir("shared");

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(NUM_THREADS);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errors = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        // All threads try to create files in same directory
        for (int i = 0; i < NUM_THREADS; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    boolean result = fs.touch("shared/file" + threadId + ".txt");
                    if (result) {
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    errors.incrementAndGet();
                    e.printStackTrace();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        doneLatch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        FileSystemEntity sharedDir = root.getChild("shared");
        int fileCount = sharedDir != null && sharedDir.isDirectory()
                ? ((Directory) sharedDir).getChildren().size()
                : 0;

        assertTest("No exceptions during concurrent touch", errors.get() == 0);
        assertTest("All touch operations should succeed", successCount.get() == NUM_THREADS);
        assertTest("Shared directory should have " + NUM_THREADS + " files", fileCount == NUM_THREADS);

        System.out.println("  Threads: " + NUM_THREADS);
        System.out.println("  Successful touch: " + successCount.get());
        System.out.println("  Files created: " + fileCount);
        System.out.println("  Errors: " + errors.get());
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════
    // TEST 14: Concurrent File Write Operations
    // ═══════════════════════════════════════════════════════════════════
    private static void testConcurrentFileWrite() throws InterruptedException {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("TEST 14: Concurrent File Write Operations");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        final int NUM_THREADS = 50;
        final int NUM_WRITES = 100;
        final Directory root = new Directory("", null);
        final File sharedFile = new File("shared.txt", root);
        root.addChild(sharedFile);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(NUM_THREADS);
        AtomicInteger writeCount = new AtomicInteger(0);
        AtomicInteger errors = new AtomicInteger(0);
        List<String> writtenContents = Collections.synchronizedList(new ArrayList<>());

        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        // Multiple threads write to the same file
        for (int i = 0; i < NUM_THREADS; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    for (int j = 0; j < NUM_WRITES / NUM_THREADS; j++) {
                        String content = "Thread" + threadId + "-Write" + j;
                        sharedFile.setContent(content);
                        writtenContents.add(content);
                        writeCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    errors.incrementAndGet();
                    e.printStackTrace();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        doneLatch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        // File should have valid content (from one of the writes)
        String finalContent = sharedFile.getContent();
        boolean validContent = finalContent.startsWith("Thread") && finalContent.contains("Write");

        assertTest("No exceptions during concurrent writes", errors.get() == 0);
        assertTest("All write operations should complete", writeCount.get() == NUM_WRITES);
        assertTest("File should have valid content", validContent);
        assertTest("File size should match content length", sharedFile.getSize() == finalContent.length());

        System.out.println("  Threads: " + NUM_THREADS);
        System.out.println("  Total writes: " + writeCount.get());
        System.out.println("  Final content: " + finalContent);
        System.out.println("  Errors: " + errors.get());
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════
    // TEST 15: Concurrent Mixed Operations
    // ═══════════════════════════════════════════════════════════════════
    private static void testConcurrentMixedOperations() throws InterruptedException {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("TEST 15: Concurrent Mixed Operations (mkdir, touch, delete)");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        final int NUM_OPERATIONS = 100;
        final Directory root = new Directory("", null);
        final FileService fs = new FileService(root);

        // Pre-create some directories
        for (int i = 0; i < 5; i++) {
            fs.mkdir("predir" + i);
        }

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(NUM_OPERATIONS);
        AtomicInteger mkdirOps = new AtomicInteger(0);
        AtomicInteger touchOps = new AtomicInteger(0);
        AtomicInteger deleteOps = new AtomicInteger(0);
        AtomicInteger errors = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(20);

        for (int i = 0; i < NUM_OPERATIONS; i++) {
            final int opId = i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    int operation = opId % 3;

                    switch (operation) {
                        case 0: // mkdir
                            fs.mkdir("newdir" + opId);
                            mkdirOps.incrementAndGet();
                            break;
                        case 1: // touch
                            fs.touch("file" + opId + ".txt");
                            touchOps.incrementAndGet();
                            break;
                        case 2: // delete
                            FileSystemEntity entity = root.getChild("predir" + (opId % 5));
                            if (entity != null) {
                                entity.delete();
                            }
                            deleteOps.incrementAndGet();
                            break;
                    }
                } catch (Exception e) {
                    errors.incrementAndGet();
                    e.printStackTrace();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        doneLatch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        assertTest("No exceptions during mixed concurrent operations", errors.get() == 0);
        assertTest("All operations should be counted",
                mkdirOps.get() + touchOps.get() + deleteOps.get() == NUM_OPERATIONS);

        System.out.println("  Total operations: " + NUM_OPERATIONS);
        System.out.println("  mkdir operations: " + mkdirOps.get());
        System.out.println("  touch operations: " + touchOps.get());
        System.out.println("  delete operations: " + deleteOps.get());
        System.out.println("  Final entity count: " + root.getChildren().size());
        System.out.println("  Errors: " + errors.get());
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════
    // HELPER METHODS
    // ═══════════════════════════════════════════════════════════════════

    private static FileService createFileService() {
        Directory root = new Directory("", null);
        return new FileService(root);
    }

    private static Directory getRootDirectory(FileService fs) {
        return fs.getRootDirectory();
    }

    private static void assertTest(String description, boolean condition) {
        if (condition) {
            System.out.println("  ✅ PASS: " + description);
            testsPassed++;
        } else {
            System.out.println("  ❌ FAIL: " + description);
            testsFailed++;
        }
    }

    private static void printTestSummary() {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                      TEST SUMMARY                            ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.printf("║  Total Tests: %-46d ║%n", testsPassed + testsFailed);
        System.out.printf("║  Passed:      %-46d ║%n", testsPassed);
        System.out.printf("║  Failed:      %-46d ║%n", testsFailed);
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        if (testsFailed == 0) {
            System.out.println("║  🎉 ALL TESTS PASSED! FileSystem API is working correctly.  ║");
        } else {
            System.out.println("║  ⚠️  SOME TESTS FAILED! Please review the failures above.   ║");
        }
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
    }
}

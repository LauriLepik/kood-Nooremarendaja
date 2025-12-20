import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Automatically compiles all source and test files, then runs all tests.
public class TestRunner {
    
    // Set to true for detailed test output, false for summary only
    private static final boolean DEBUG = true;
    
    private static final String JUNIT_JAR = "junit-platform-console-standalone-1.9.3.jar";
    private static final String BUILD_DIR = "build";
    
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  Banking Application - Test Suite Runner");
        System.out.println("==================================================\n");
        
        try {
            // Step 1: Compile production code
            System.out.println("Step 1/3: Compiling production code...");
            if (!compileProduction()) {
                System.err.println("Error: Production compilation failed.");
                System.exit(1);
            }
            System.out.println("[OK] Production code compiled\n");
            
            // Step 2: Compile test code
            System.out.println("Step 2/3: Compiling test code...");
            if (!compileTests()) {
                System.err.println("Error: Test compilation failed.");
                System.exit(1);
            }
            
            System.out.println("Compilation successful. Executing tests...\n");
            
            // Step 3: Run all tests
            System.out.println("Step 3/3: Running all tests...");
            System.out.println("==================================================\n");
            int exitCode = runTests();
            
            System.out.println("\n==================================================");
            if (exitCode == 0) {
                System.out.println("[OK] All tests passed!");
            } else {
                System.out.println("[FAIL] Some tests failed");
            }
            System.out.println("==================================================");
            
            System.exit(exitCode);
            
        } catch (Exception e) {
            System.err.println("Fatal Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private static boolean compileProduction() throws Exception {
        List<String> command = new ArrayList<>();
        command.add("javac");
        command.add("-cp");
        command.add("." + File.pathSeparator + JUNIT_JAR);
        command.add("-d");
        command.add(BUILD_DIR);
        
        // Add all source files dynamically
        addJavaFiles(command, "exception");
        addJavaFiles(command, "model");
        addJavaFiles(command, "service");
        addJavaFiles(command, "util");
        command.add("BankingApp.java");
        
        return runCommand(command);
    }
    
    private static boolean compileTests() throws Exception {
        String classpath = "." + File.pathSeparator + JUNIT_JAR + File.pathSeparator + BUILD_DIR;
        
        // Compile all tests together for simplicity and speed
        List<String> command = new ArrayList<>();
        command.add("javac");
        command.add("-cp");
        command.add(classpath);
        command.add("-d");
        command.add(BUILD_DIR);
        
        addJavaFiles(command, "test/exception");
        addJavaFiles(command, "test/model");
        addJavaFiles(command, "test/service");
        addJavaFiles(command, "test/util");
        addJavaFiles(command, "test/integration");
        
        return runCommand(command);
    }

    private static void addJavaFiles(List<String> command, String directory) throws IOException {
        Path start = Paths.get(directory);
        if (!Files.exists(start)) {
            System.out.println("Warning: Directory not found: " + directory);
            return;
        }

        try (Stream<Path> stream = Files.walk(start)) {
            List<String> files = stream
                .filter(p -> p.toString().endsWith(".java"))
                .map(Path::toString)
                .collect(Collectors.toList());
            command.addAll(files);
        }
    }
    
    private static int runTests() throws Exception {
        String detailLevel = DEBUG ? "tree" : "summary";
        
        ProcessBuilder pb = new ProcessBuilder(
            "java", "-jar", JUNIT_JAR,
            "--class-path", BUILD_DIR,
            "--scan-classpath",
            "--details", detailLevel
        );
        pb.inheritIO();  // Show output in real-time
        Process process = pb.start();
        return process.waitFor();
    }
    
    private static boolean runCommand(List<String> command) throws Exception {
        // Prepare builder
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.inheritIO();  // Show output in real-time
        Process process = pb.start();
        int exitCode = process.waitFor();
        return exitCode == 0;
    }
}

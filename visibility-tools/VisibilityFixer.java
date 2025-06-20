import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * VisibilityFixer - A tool to automatically add 'public' visibility modifiers
 * to Kotlin declarations in explicit API mode.
 */
public class VisibilityFixer {
    
    private static final Pattern CLASS_PATTERN = Pattern.compile(
            "^(\\s*)(class|interface|object|data class|enum class|sealed class)(\\s+\\w+.*)$", 
            Pattern.MULTILINE);
    
    private static final Pattern FUNCTION_PATTERN = Pattern.compile(
            "^(\\s*)(fun)(\\s+\\w+.*)$", 
            Pattern.MULTILINE);
    
    private static final AtomicInteger filesProcessed = new AtomicInteger(0);
    private static final AtomicInteger filesModified = new AtomicInteger(0);
    
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: java VisibilityFixer <directory>");
            System.exit(1);
        }
        
        Path directory = Paths.get(args[0]);
        if (!Files.isDirectory(directory)) {
            System.out.println("Error: " + args[0] + " is not a directory");
            System.exit(1);
        }
        
        boolean dryRun = args.length > 1 && args[1].equals("--dry-run");
        
        System.out.println("Scanning Kotlin files in: " + directory.toAbsolutePath());
        System.out.println(dryRun ? "Dry run mode - no files will be modified" : "Files will be modified");
        
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".kt")) {
                    processFile(file, dryRun);
                }
                return FileVisitResult.CONTINUE;
            }
        });
        
        System.out.println("Completed processing " + filesProcessed.get() + " files");
        System.out.println("Modified " + filesModified.get() + " files");
    }
    
    private static void processFile(Path file, boolean dryRun) throws IOException {
        filesProcessed.incrementAndGet();
        
        String content = Files.readString(file);
        String modified = addVisibilityModifiers(content);
        
        if (!content.equals(modified)) {
            filesModified.incrementAndGet();
            System.out.println("Fixing visibility in: " + file);
            
            if (!dryRun) {
                Files.writeString(file, modified);
            }
        }
    }
    
    private static String addVisibilityModifiers(String content) {
        // Add 'public' to classes, interfaces, etc.
        Matcher classMatcher = CLASS_PATTERN.matcher(content);
        StringBuffer sb = new StringBuffer();
        
        while (classMatcher.find()) {
            // Skip if already has a visibility modifier
            String indent = classMatcher.group(1);
            String keyword = classMatcher.group(2);
            String rest = classMatcher.group(3);
            
            // Skip if the line already has a visibility modifier
            if (content.substring(Math.max(0, classMatcher.start() - 10), classMatcher.start())
                    .matches(".*\\b(public|private|protected|internal)\\b.*")) {
                classMatcher.appendReplacement(sb, indent + keyword + rest);
            } else {
                classMatcher.appendReplacement(sb, indent + "public " + keyword + rest);
            }
        }
        classMatcher.appendTail(sb);
        
        // Add 'public' to functions
        content = sb.toString();
        Matcher funcMatcher = FUNCTION_PATTERN.matcher(content);
        sb = new StringBuffer();
        
        while (funcMatcher.find()) {
            // Skip if already has a visibility modifier
            String indent = funcMatcher.group(1);
            String keyword = funcMatcher.group(2);
            String rest = funcMatcher.group(3);
            
            // Skip if the line already has a visibility modifier
            if (content.substring(Math.max(0, funcMatcher.start() - 10), funcMatcher.start())
                    .matches(".*\\b(public|private|protected|internal)\\b.*")) {
                funcMatcher.appendReplacement(sb, indent + keyword + rest);
            } else {
                funcMatcher.appendReplacement(sb, indent + "public " + keyword + rest);
            }
        }
        funcMatcher.appendTail(sb);
        
        return sb.toString();
    }
}

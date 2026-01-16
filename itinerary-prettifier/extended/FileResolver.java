package extended;

import java.io.File;

// Utility for intelligent file path resolution and directory traversal.
public class FileResolver {
    
    // Last message about what we found (or didn't find).
    private String message = null;
    
    public String getMessage() {
        return message;
    }
    
    // Resolve coordinate lookup file path.
    public File resolveLookup(String path) {
        return resolve(path, "airport-lookup.csv", "airport database", ".csv");
    }
    
    // Resolve input itinerary file path.
    public File resolveInput(String path) {
        return resolve(path, "input.txt", "input file", ".txt");
    }
    
    // Resolve output file path based on output type.
    public File resolveOutput(String path, String type) {
        File f = new File(path);
        // If they gave us a folder, make a filename for them
        if (f.isDirectory()) {
            return new File(f, "output." + (type != null ? type : "txt"));
        }
        // If the file has no extension, add one based on output type
        String name = f.getName();
        if (!name.contains(".")) {
            String ext = (type != null) ? type : "txt";
            // Normalize html variants
            if (ext.equals("htmlf") || ext.equals("htmlfancy")) ext = "html";
            return new File(f.getParent(), name + "." + ext);
        }
        return f;
    }

    private File resolve(String path, String defaultName, String typeLabel, String defaultExt) {
        message = null;
        File f = new File(path);
        
        // Handle paths that start with / or \ (root relative)
        if (path.startsWith("/") || path.startsWith("\\")) {
             File rootRelative = new File("." + path);
             if (rootRelative.exists()) {
                 message = String.format("Resolved %s via root-relative path: %s", 
                                          typeLabel, getCleanPath(rootRelative));
                 return rootRelative;
             }
        }
        
        // Does it exist as-is?
        if (f.exists() && f.isFile()) {
            return f;
        }
        
        // Try adding the extension
        File withExt = new File(path + defaultExt);
        if (withExt.exists()) {
            message = String.format("No direct path to %s provided, found and using: %s", 
                                     typeLabel, getCleanPath(withExt));
            return withExt;
        }
        
        // Hunt through subdirectories
        File found = crawl(".", path, defaultExt, 5);
        if (found != null) {
            message = String.format("No direct path to %s provided, found and using: %s", 
                                     typeLabel, getCleanPath(found));
            return found;
        }
        
        // Maybe it's in the parent folder?
        File parent = new File("..", path);
        if (parent.exists()) {
            message = String.format("No direct path to %s provided, found and using: %s", 
                                     typeLabel, getCleanPath(parent));
            return parent;
        }
        
        return f;
    }

    private File crawl(String dir, String filename, String ext, int depth) {
        if (depth <= 0) return null;
        File d = new File(dir);
        File[] files = d.listFiles();
        if (files == null) return null;
        
        for (File f : files) {
            if (f.isDirectory()) {
                // Skip common system and build directories to speed up search
                String name = f.getName();
                if (name.equals("build") || name.equals(".git") || name.equals(".gradle")) continue;
                
                File res = crawl(f.getPath(), filename, ext, depth - 1);
                if (res != null) return res;
            } else {
                String name = f.getName();
                if (name.equals(filename) || name.equals(filename + ext)) {
                    return f;
                }
            }
        }
        return null;
    }

    private String getCleanPath(File f) {
        try {
            return f.getCanonicalPath();
        } catch (java.io.IOException e) {
            return f.getAbsolutePath();
        }
    }
}

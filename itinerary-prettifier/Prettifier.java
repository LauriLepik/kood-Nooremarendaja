
import extended.AirportLookup; // Moved to extended from root for functionality
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

// CLI entry point - parses args and kicks off processing.
public class Prettifier {
    
    private static final String USAGE_BASIC = "itinerary usage:\n$ java Prettifier.java ./input.txt ./output.txt ./airport-lookup.csv";
    
    private static final String USAGE_EXTENDED = """
            Itinerary Prettifier CLI
            $ java Prettifier.java [input] [output] [airport-lookup] [options]
            
            Arguments:
              [input]           Input itinerary file
              [output]          Output file
              [airport-lookup]  Airport codes CSV
            
            Options:
              -h                Show this help message
              -e, --extended    Enable advanced formatting and data enrichment
              -o <type>         Output format: txt, md, html, htmlf (default: txt)
              -p                Print the processed result to console
              -v                Display detailed processing statistics
              --tz <zone>       Convert flight times to a specific timezone
              --api-key <key>   Optional Geoapify key for better timezone precision""";
    
    public static void main(String[] args) {
        // Check if any extended flags are present
        boolean isExtended = hasFlag(args, "-e", "--extended", "-o", "-v", "-p", "--tz", "--api-key");
        
        if (hasFlag(args, "-h", "--help")) {
            System.out.println(isExtended ? USAGE_EXTENDED : USAGE_BASIC);
            return;
        }
        
        String[] paths = removeFlags(args);
        
        if (paths.length != 3) {
            System.out.println(isExtended ? USAGE_EXTENDED : USAGE_BASIC);
            return;
        }
        
        File inputFile, outputFile, lookupFile;
        extended.ProcessingOptions options = new extended.ProcessingOptions();
        
        if (isExtended) {
            extended.FileResolver resolver = new extended.FileResolver();
            String outputType = getFlagValue(args, "-o");
            if (outputType == null) outputType = "txt";
            
            options.setOutputType(outputType);
            options.setPrintToStdout(hasFlag(args, "-p"));
            options.setVerbose(hasFlag(args, "-v"));
            options.setTimezone(getFlagValue(args, "--tz"));
            options.setDateFormat(getFlagValue(args, "--format"));
            options.setApiKey(getFlagValue(args, "--api-key"));
            
            // Try to find files even with partial paths
            inputFile = resolver.resolveInput(paths[0]);
            if (resolver.getMessage() != null) System.out.println(resolver.getMessage());
            
            outputFile = resolver.resolveOutput(paths[1], outputType);
            
            lookupFile = resolver.resolveLookup(paths[2]);
            if (resolver.getMessage() != null) System.out.println(resolver.getMessage());
        } else {
            // Basic mode - just use paths as given
            inputFile = new File(paths[0]);
            outputFile = new File(paths[1]);
            lookupFile = new File(paths[2]);
        }
        
        // Make sure we have our files
        if (!inputFile.exists()) {
            System.err.println("Error: Input file not found: " + paths[0]);
            return;
        }
        
        if (!lookupFile.exists()) {
            System.err.println("Error: Airport lookup database not found: " + paths[2]);
            return;
        }
        
        AirportLookup lookup;
        try {
            lookup = new AirportLookup(lookupFile.getPath());
            if (lookup.isMalformed()) {
                System.err.println("Error: The airport lookup file is malformed.");
                return;
            }
        } catch (IOException e) {
            System.err.println("Error: Failed to read airport lookup file.");
            return;
        }
        
        String rawContent;
        try {
            rawContent = Files.readString(inputFile.toPath());
        } catch (IOException e) {
            System.err.println("Error: Failed to read input file.");
            return;
        }
        
        // Process the itinerary
        ItineraryProcessor processor = new ItineraryProcessor(lookup);
        String finalOutput = processor.process(rawContent);
        
        if (isExtended) {
            extended.ItineraryParser parser = new extended.ItineraryParser(lookup);
            java.util.List<extended.Flight> flights = parser.parse(rawContent);
            
            extended.ExtendedProcessor extProcessor = new extended.ExtendedProcessor(options, lookup);
            finalOutput = extProcessor.generate(flights);
            
            // Print to console if requested
            if (options.isPrintToStdout()) {
                String consoleOutput;
                
                // If the file output isn't text, we generate a fresh text version for the console
                if (!options.getOutputType().equalsIgnoreCase("txt")) {
                     consoleOutput = extended.format.TextFormatter.format(flights, extended.Branding.getRandomWish(), options);
                } else {
                     consoleOutput = finalOutput;
                }
                
                System.out.println(extProcessor.colorize(consoleOutput));
            }
            
            extProcessor.printWarnings(flights);
            extProcessor.printStats(flights);
        }
        
        try {
            Files.writeString(outputFile.toPath(), finalOutput);
        } catch (IOException e) {
            System.err.println("Error: could not write the output file.");
        }
    }
    
    private static boolean hasFlag(String[] args, String... flags) {
        for (String arg : args) {
            for (String flag : flags) {
                if (arg.equals(flag)) return true;
            }
        }
        return false;
    }
    
    private static String getFlagValue(String[] args, String flag) {
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals(flag)) return args[i + 1];
        }
        return null;
    }
    
    private static String[] removeFlags(String[] args) {
        java.util.List<String> result = new java.util.ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-")) {
                // Skip the value part too for flags like "-o txt"
                if (args[i].equals("-o") || args[i].equals("--tz") || 
                    args[i].equals("--format") || args[i].equals("--api-key")) {
                    i++;
                }
            } else {
                result.add(args[i]);
            }
        }
        return result.toArray(new String[0]);
    }
}

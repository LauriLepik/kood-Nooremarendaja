package extended;

// All the settings that control extended mode behavior.
public class ProcessingOptions {
    
    private boolean verbose = false;
    private boolean printToStdout = false;
    private String outputType = "txt";
    private String timezone = null;
    private String dateFormat = null;
    private String apiKey = null;
    
    public boolean isVerbose() { 
        return verbose; 
    }
    
    public void setVerbose(boolean verbose) { 
        this.verbose = verbose; 
    }
    
    public boolean isPrintToStdout() { 
        return printToStdout; 
    }
    
    public void setPrintToStdout(boolean printToStdout) { 
        this.printToStdout = printToStdout; 
    }
    
    public String getOutputType() { 
        return outputType; 
    }
    
    public void setOutputType(String outputType) { 
        this.outputType = outputType; 
    }
    
    public String getTimezone() { 
        return timezone; 
    }
    
    public void setTimezone(String timezone) { 
        this.timezone = timezone; 
    }
    
    public String getDateFormat() { 
        return dateFormat; 
    }
    
    public void setDateFormat(String dateFormat) { 
        this.dateFormat = dateFormat; 
    }
    
    public String getApiKey() { 
        return apiKey; 
    }
    
    public void setApiKey(String apiKey) { 
        this.apiKey = apiKey; 
    }
}

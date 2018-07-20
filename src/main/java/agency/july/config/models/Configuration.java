package agency.july.config.models;

import static java.lang.String.format;

import java.util.List;
import java.util.Map;

public final class Configuration {
	private static Map< String, BrowserProps > browsers;
	private static Map< String, Map< String, String > > csss;
	private static Map< Integer, String > patterns;
	private static Map< String, Boolean > logger;
	private static List< String > runtests;

	public static Map<String, BrowserProps> getBrowsers() {
		return browsers;
	}

	public void setBrowsers(Map<String, BrowserProps> browsers) {
		Configuration.browsers = browsers;
	}

	public static Map<String, Map<String, String>> getCsss() {
		return csss;
	}

	public void setCsss(Map<String, Map<String, String>> csss) {
		Configuration.csss = csss;
	}
	
	public static Map< Integer, String > getPatterns() {
        return patterns;
    }
 
	public void setPatterns(Map< Integer, String > patterns) {
		Configuration.patterns = patterns;
    }

	public static Map< String, Boolean > getLogger() {
        return logger;
    }
 
	public void setLogger(Map< String, Boolean > logger) {
		Configuration.logger = logger;
    }

    public static List<String> getRuntests() {
        return runtests;
    }

    public void setRuntests(List< String > runtests) {
    	Configuration.runtests = runtests;
	}

    @Override
    public String toString() {
        return new StringBuilder()
            .append( format( "Browsers: %s\n", browsers ) )
            .append( format( "CSS selectors: %s\n", csss ) )
            .append( format( "Patterns: %s\n", patterns ) )
            .append( format( "Logger: %s\n", logger ) )
            .append( format( "RunningTests: %s\n", runtests ) )
            .toString();
    }
}

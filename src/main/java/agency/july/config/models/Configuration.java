package agency.july.config.models;

import static java.lang.String.format;

import java.util.List;
import java.util.Map;

public final class Configuration {
//	private static Map< String, String > logins;
//	private static Map< String, String > passwds;
//	private static Map< String, String > usernames;
//	private static Map< String, String > urls;
//	private static Map< String, String > pathto;
//	private static List < String > images;
//	private static EmailParams email;
	private static DriverType browser;
	private static Map< String, Integer > dimension;
	private static Map< String, Map< String, String > > csss;
	private static Map< String, List < Map < String, String > > > flowsss;
	private static Map< Integer, String > patterns;
	private static Map< String, Boolean > logger;
	private static List< String > runtests;
/*	
	public static Map< String, String > getLogins() {
        return logins;
    }
 
    public void setLogins(Map< String, String > logins) {
    	Configuration.logins = logins;
    }
    
	public static Map< String, String > getPasswds() {
        return passwds;
    }
 
    public void setPasswds(Map< String, String > passwds) {
    	Configuration.passwds = passwds;
    }

	public static Map< String, String > getUsernames() {
        return usernames;
    }
 
    public void setUsernames(Map< String, String > usernames) {
    	Configuration.usernames = usernames;
    }

	public static Map< String, String > getUrls() {
        return urls;
    }
 
    public void setUrls(Map< String, String > urls) {
    	Configuration.urls = urls;
    }

	public static Map< String, String > getPathto() {
        return pathto;
    }
 
    public void setPathto(Map< String, String > pathto) {
    	Configuration.pathto = pathto;
    }

	public static EmailParams getEmail() {
        return email;
    }
 
	public void setEmail(EmailParams email) {
		Configuration.email = email;
	}
*/
	public static DriverType getBrowser() {
        return browser;
    }
 
    public void setBrowser(DriverType browser) {
    	Configuration.browser = browser;
    }

	public static Map< String, Integer > getDimension() {
        return dimension;
    }
 
    public void setDimension(Map< String, Integer > dimension) {
    	Configuration.dimension = dimension;
    }

	public static Map<String, Map<String, String>> getCsss() {
		return csss;
	}

	public void setCsss(Map<String, Map<String, String>> csss) {
		Configuration.csss = csss;
	}
	
	public static Map<String, List<Map<String, String>>> getFlowsss() {
		return flowsss;
	}

	public void setFlowsss(Map<String, List<Map<String, String>>> flowsss) {
		Configuration.flowsss = flowsss;
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
/*
	public static List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		Configuration.images = images;
	}
*/
    @Override
    public String toString() {
        return new StringBuilder()
//            .append( format( "Logins: %s\n", logins ) )
//            .append( format( "Passwds: %s\n", passwds ) )
//            .append( format( "Usernames: %s\n", usernames ) )
//            .append( format( "Urls: %s\n", urls ) )
//            .append( format( "PathTo: %s\n", pathto ) )
//            .append( format( "Email: %s\n", email ) )
            .append( format( "Browser: %s\n", browser ) )
            .append( format( "Dimension: %s\n", dimension ) )
            .append( format( "CSS selectors: %s\n", csss ) )
            .append( format( "Flowsss: %s\n", flowsss ) )
            .append( format( "Patterns: %s\n", patterns ) )
            .append( format( "Logger: %s\n", logger ) )
            .append( format( "RunningTests: %s\n", runtests ) )
            .toString();
    }

}

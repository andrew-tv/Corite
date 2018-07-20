package agency.july.config.models;

import static java.lang.String.format;

import java.util.Map;

public class Accesses {

	private static Map< String, String > logins;
	private static Map< String, String > passwds;
	private static Map< String, String > usernames;
	private static Map< String, String > urls;
	private static Map< String, String > pathto;
	private static EmailParams email;
	
	public static Map<String, String> getLogins() {
		return logins;
	}

	public void setLogins(Map<String, String> logins) {
		Accesses.logins = logins;
	}

	public static Map<String, String> getPasswds() {
		return passwds;
	}

	public void setPasswds(Map<String, String> passwds) {
		Accesses.passwds = passwds;
	}

	public static Map<String, String> getUsernames() {
		return usernames;
	}

	public void setUsernames(Map<String, String> usernames) {
		Accesses.usernames = usernames;
	}

	public static Map<String, String> getUrls() {
		return urls;
	}

	public void setUrls(Map<String, String> urls) {
		Accesses.urls = urls;
	}

	public static Map<String, String> getPathto() {
		return pathto;
	}

	public void setPathto(Map<String, String> pathto) {
		Accesses.pathto = pathto;
	}

	public static EmailParams getEmail() {
		return email;
	}

	public void setEmail(EmailParams email) {
		Accesses.email = email;
	}
	
    @Override
    public String toString() {
        return new StringBuilder()
            .append( format( "Logins: %s\n", logins ) )
            .append( format( "Passwds: %s\n", passwds ) )
            .append( format( "Usernames: %s\n", usernames ) )
            .append( format( "Urls: %s\n", urls ) )
            .append( format( "PathTo: %s\n", pathto ) )
            .append( format( "Email: %s\n", email ) )
            .toString();
    }
}

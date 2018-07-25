package agency.july.config.models;

import static java.lang.String.format;

public class Database {
	private String user;
    private String pass;
    private String db;
	
    public String getUser() {
		return user;
	}
    
	public void setUser(String user) {
		this.user = user;
	}
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
	public String getDb() {
		return db;
	}
	public void setDb(String db) {
		this.db = db;
	}
	
    @Override
    public String toString() {
        return new StringBuilder()
            .append( format( "User: %s\n", user ) )
            .append( format( "Password: %s\n", "pass" ) )
            .append( format( "Name: %s\n", db ) )
            .toString();
    }

}

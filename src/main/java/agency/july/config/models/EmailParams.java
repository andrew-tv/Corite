package agency.july.config.models;

import static java.lang.String.format;

public class EmailParams {

    private String protocol;
    private String store;
    private String connect;
    private String folder;
    private String account;
    private String password;
  
    public String getProtocol() {
        return protocol;
    }
 
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }
 
    public String getConnect() {
        return connect;
    }

    public void setConnect(String connect) {
        this.connect = connect;
    }
 
    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }
 
    public void setAccount(String account) {
        this.account = account;
    }
 
    public String getAccount() {
        return account;
    }

    public void setPassword(String password) {
        this.password = password;
    }
 
    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append( format( "Protocol: %s\n", protocol ) )
                .append( format( "Store: %s\n", store ) )
                .append( format( "Connect: %s\n", connect ) )
                .append( format( "Folder: %s\n", folder ) )
                .append( format( "Account: %s\n", account ) )
                .append( format( "Password: %s\n", password ) )
                .toString();
    }

}

package agency.july.config.models;

import static java.lang.String.format;

public class State {
	
	private String csss;
	private int hash;
	
	public String getCsss() {
		return csss;
	}
	public void setCsss(String csss) {
		this.csss = csss;
	}
	public int getHash() {
		return hash;
	}
	public void setHash(int hash) {
		this.hash = hash;
	}
	
    @Override
    public String toString() {
        return new StringBuilder()
                .append( format( "Csss: %s\n", csss ) )
                .append( format( "Hash: %s\n", hash ) )
               .toString();
    }
}

package agency.july.config.models;

import static java.lang.String.format;

public class Action {
	
	private String csss;
	private ActionType action;
	
	public String getCsss() {
		return csss;
	}
	public void setCsss(String csss) {
		this.csss = csss;
	}
	public ActionType getAction() {
		return action;
	}
	public void setAction(ActionType action) {
		this.action = action;
	}

    @Override
    public String toString() {
        return new StringBuilder()
                .append( format( "Csss: %s\n", csss ) )
                .append( format( "Action: %s\n", action ) )
               .toString();
    }
}

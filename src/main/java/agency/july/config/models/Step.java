package agency.july.config.models;

import static java.lang.String.format;

public class Step {
	
    private State before;
    private Action action;
    private State after;

	public State getBefore() {
		return before;
	}

	public void setBefore(State before) {
		this.before = before;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public State getAfter() {
		return after;
	}

	public void setAfter(State after) {
		this.after = after;
	}
	
	@Override
    public String toString() {
        return new StringBuilder()
                .append( format( "Before: %s\n", before ) )
                .append( format( "Action: %s\n", action ) )
                .append( format( "After: %s\n", after ) )
               .toString();
    }
}

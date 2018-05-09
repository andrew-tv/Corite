package agency.july.flow;


public class TestFailedException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public TestFailedException() {	}
	TestFailedException(String msg) { super(msg); }
}

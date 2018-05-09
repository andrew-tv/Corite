package agency.july.logger;

import static agency.july.logger.Logevent.FAILED;
import static agency.july.logger.Logevent.PASSED;

import org.openqa.selenium.WebDriver;

public class Assert {

	WebDriver driver;

	String pattern = "";
	String example = "";
	String msg = "";
	Exception exception = null;
	
	public Assert withCompare(String pattern, String example) {
		this.pattern = pattern;
		this.example = example;
		return this;
	}

	public Assert withDriver(WebDriver driver) {
		this.driver = driver;
		return this;
	}

	public Assert withMsg(String msg) {
		this.msg = msg;
		return this;
	}
	
	public Assert withException(Exception exception) {
		this.exception = exception;
		return this;
	}
	
	private void check(boolean res) throws Exception {
		if ( res ) 
			PASSED.writeln(msg);
		else {
			FAILED.writeln(msg);
			if (exception != null) throw exception;
		}
	}
	
	public void assertEquals() throws Exception {
		check(pattern.equals(example));
	}

	public void assertContains() throws Exception {
		check(pattern.contains(example));
	}

}

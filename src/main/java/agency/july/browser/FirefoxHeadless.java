package agency.july.browser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import agency.july.config.models.BrowserProps;

public class FirefoxHeadless implements Browser {

	@Override
	public WebDriver createDriver(BrowserProps props) {
    	System.setProperty("webdriver.gecko.driver", props.getFile()); //"/Users/andrew/bin/geckodriver"
    	FirefoxBinary firefoxBinary = new FirefoxBinary();
    	firefoxBinary.addCommandLineOptions("--headless");
    	FirefoxOptions firefoxOptions = new FirefoxOptions();
    	firefoxOptions.setBinary(firefoxBinary);   	
 		return new FirefoxDriver(firefoxOptions);
	}

}

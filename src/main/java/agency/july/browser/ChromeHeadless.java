package agency.july.browser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import agency.july.config.models.BrowserProps;

public class ChromeHeadless implements Browser {

	@Override
	public WebDriver createDriver(BrowserProps props) {
    	System.setProperty("webdriver.chrome.driver", props.getFile()); // "/Users/andrew/bin/chromedriver"
    	ChromeOptions chromeOptions = new ChromeOptions();
    	chromeOptions.addArguments("headless");
    	chromeOptions.addArguments("silent");  // This don't work
		return new ChromeDriver(chromeOptions);
	}
}

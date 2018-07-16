package agency.july.browser;

import org.openqa.selenium.WebDriver;

import agency.july.config.models.BrowserProps;

public interface Browser {
	public WebDriver createDriver(BrowserProps props);
}

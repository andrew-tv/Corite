package agency.july.flow;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public interface IFlow {
	public WebDriver getDriver();
	public void setDriver(WebDriver driver);
	public int getScreenshotHash();
	public int getCurrentSlideNumber();
	public void makeScreenshot();
	public void makeScreenshot(String prefix);
	public void sleep(int millis);
	void waitForStableLocation(By by, int firstWait);
	void waitForStableLocation(WebElement el, int firstWait);
}

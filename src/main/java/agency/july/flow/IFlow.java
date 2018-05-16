package agency.july.flow;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public interface IFlow {
	public WebDriver getDriver();
	public void setDriver(WebDriver driver);
	public int getScreenshotHash();
	public void incSlideNumber();
	public int getCurrentSlide();
//	public void waitForHtmlHash();
	public void waitForHtmlHash(By by);
	public void makeScreenshot();
	int getExpectedHtmlHash();
}

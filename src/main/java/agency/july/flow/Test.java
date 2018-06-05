package agency.july.flow;

import static agency.july.logger.Logevent.*;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import agency.july.config.models.Accesses;
import agency.july.config.models.Configuration;
import agency.july.config.models.DriverType;
import agency.july.webelements.Element;

public abstract class Test {
	
    protected WebDriver driver;
    protected Flow flow;
    protected int defaultImplicitly = 10;
	
	public Test ( Flow flow ) {
    	setDriver( Configuration.getBrowser() );
    	Dimension dim = new Dimension(Configuration.getDimension().get("width"), Configuration.getDimension().get("hight"));
    	this.driver.manage().window().setSize( dim );
    	this.driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    	this.driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
    	this.flow = flow;
    }
    
    public void setDriver(DriverType driverType) {
	    switch ( driverType ) {       
	    case FIREFOX : 
	    	System.setProperty("webdriver.gecko.driver","./geckodriver");
	    	this.driver = new FirefoxDriver();
	    	break;
	    	
	    case FIREFOX_HEADLESS : 
	    	System.setProperty("webdriver.gecko.driver","./geckodriver");
	    	FirefoxBinary firefoxBinary = new FirefoxBinary();
	    	firefoxBinary.addCommandLineOptions("--headless");
	    	FirefoxOptions firefoxOptions = new FirefoxOptions();
	    	firefoxOptions.setBinary(firefoxBinary);
	    	this.driver = new FirefoxDriver(firefoxOptions);
	    	break;
	    	
	    case CHROME : 
	    	System.setProperty("webdriver.chrome.driver", "./chromedriver");
	    	this.driver = new ChromeDriver();
		break;
	
	    case CHROME_HEADLESS : 
	    	System.setProperty("webdriver.chrome.driver", "./chromedriver");
	    	ChromeOptions chromeOoptions = new ChromeOptions();
	    	chromeOoptions.addArguments("headless");
	    	chromeOoptions.addArguments("silent");  // This don't work
	    	this.driver = new ChromeDriver(chromeOoptions);
		break;
		
	    default:
	    	this.driver = null;
	    }	
	}
    
    public void goHome() { 
    	driver.get(Accesses.getUrls().get("base"));
    }
    
	public String getBaseUrl() {
		return Accesses.getUrls().get("base");
	}
	
	public void checkFormFillingError() {
		
    	this.driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);

		Element matError = new Element(this.flow, By.cssSelector("mat-error"));
		if ( matError.exists() ) {
			String errorText = matError.getText();
			FAILED.writeln("Form filling error: '" + errorText + "'" );
			if ( !errorText.contains("email") ) {
				throw new TestFailedException();					
			}
		}
		
		matError = new Element(this.flow, By.cssSelector(".mat-error"));
		if ( matError.exists() ) {
			FAILED.writeln("Form filling error: '" + matError.getText() + "'" );
			throw new TestFailedException();					
		}
		
    	this.driver.manage().timeouts().implicitlyWait(this.defaultImplicitly, TimeUnit.SECONDS);
	}	
    
    public void checkFirstCampaignInList() {

		WebDriverWait wait = new WebDriverWait(driver, 10);
    	
    	flow.setDriver(driver);
    	goHome();
		wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector("page-explore-list")) );
//		wait.until( ExpectedConditions.textToBe(By.cssSelector("div.audio > div > div.slider__max"), "00:27") );

    	if ( flow.checkHtmlHash() ) 
    		PASSED.writeln("The first campaign in the list is checked after moderation");
    	else {
			FAILED.writeln("The first campaign in the list or its hash is wrong after moderation");
			flow.makeErrorScreenshot();
    	}
    }
    
    public void teardown () {
    	driver.quit();
    }
    
}

package agency.july.flow;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import agency.july.config.models.Configuration; // agency.july.test.config.models.Configuration;
import agency.july.config.models.DriverType; // agency.july.test.config.models.DriverType;

public abstract class Test {
	
    protected WebDriver driver;
    protected Flow flow;
//    protected static Configuration config;
	
//	public Test() { }
	
	public Test ( Flow flow ) {
    	setDriver( Configuration.getBrowser() );
    	Dimension dim = new Dimension(Configuration.getDimension().get("width"), Configuration.getDimension().get("hight"));
    	this.driver.manage().window().setSize( dim );
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
	    	this.driver = new ChromeDriver(chromeOoptions);
		break;
		
	    default:
	    	this.driver = null;
	    }
	
	}
    
    public void goHome() { }
    
    public void teardown () {
    	driver.quit();
    }
    
}

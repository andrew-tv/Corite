package agency.july.factory;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import agency.july.browser.*;
import agency.july.config.models.BrowserProps;

public class BrowserFactory {

	public static WebDriver getDriver( BrowserProps props ) {
		
		WebDriver driver;
		
    	Dimension dim = new Dimension( props.getWidth(), props.getHight() );

	    switch ( props.getType() ) {       
	    case FIREFOX : 
	    	System.setProperty("webdriver.gecko.driver", props.getFile() ); //"/Users/andrew/bin/geckodriver"
	    	driver = new FirefoxDriver();
	    	break;
	    	
	    case FIREFOX_HEADLESS : 
	    	driver = new FirefoxHeadless().createDriver(props);
	    	break;
	    	
	    case CHROME : 
	    	System.setProperty("webdriver.chrome.driver", props.getFile()); //"/Users/andrew/bin/chromedriver"
	    	driver = new ChromeDriver();
	    	break;
	
	    case CHROME_HEADLESS : 
	    	driver = new ChromeHeadless().createDriver(props);
	    	break;
		
	    default:
	    	return null;
	    }
	    
    	driver.manage().window().setSize( dim );
    	driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
    	driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
    	
    	return driver;
	}


}

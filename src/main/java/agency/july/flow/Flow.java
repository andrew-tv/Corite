package agency.july.flow;

import static agency.july.logger.Logevent.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import agency.july.config.models.Accesses;

public class Flow implements IFlow {
	
	private WebDriver driver;
	private String flowName;
	private String pathToScreenshots;
	private int currentSlideNumber = 0;
	private int currentError = 0;
	
	public Flow (String flowName) {
		this.flowName = flowName;
		this.pathToScreenshots = Accesses.getPathto().get("screenshots")/* + Configuration.getBrowser().toString() + "/"*/;
	}
	
	public String getFlowName() {
		return flowName;
	}

	public int getCurrentSlideNumber() {
		return currentSlideNumber;
	}
	
	public void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
	}

	public String getPathToScreenshots() {
		return pathToScreenshots;
	}

	@Override
	public WebDriver getDriver() {
		return driver;
	}

	@Override
	public void setDriver(WebDriver driver) {
		this.driver = driver;		
	}

	@Override
	public void makeScreenshot() {
		byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
		File screenshot = OutputType.FILE.convertFromPngBytes(bytes);
		
		try {
			FileUtils.copyFile(screenshot, new File(this.pathToScreenshots
					+ flowName + "/"
					+ "slide_" + this.currentSlideNumber 
					+ "_hash_" + java.util.Arrays.hashCode(bytes) 
					+ "_" + screenshot.getName().substring(15) 
					));
		} catch (IOException e) {
			System.err.println("Impossible to store a screenshot");	
			System.err.println(this.getClass().getName() + " >> Page title: " + driver.getTitle());			
			e.printStackTrace();
		}
	}

	@Override
	public void makeScreenshot(String prefix) {
		byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
		File screenshot = OutputType.FILE.convertFromPngBytes(bytes);
		
		try {
			FileUtils.copyFile(screenshot, new File(this.pathToScreenshots
					+ flowName + "/"
					+ prefix
					+ "_slide_" + this.currentSlideNumber 
					+ "_" + screenshot.getName().substring(15) 
					));
		} catch (IOException e) {
			System.err.println("Impossible to store a screenshot");	
			e.printStackTrace();
		}
	}
	
	public void makeErrorScreenshot() { 
	    StackTraceElement ste = Thread.currentThread().getStackTrace()[2]; // 2 - уровень отката по стеку вызовов функций
		byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
		File screenshot = OutputType.FILE.convertFromPngBytes(bytes);

		try {
			FileUtils.copyFile(screenshot, new File(this.pathToScreenshots
					+ flowName + "/"
					+ "err_" + this.currentError + "_"
			        + ste.getClassName() + "_"
			        + ste.getMethodName() + "_" 
			        + ste.getLineNumber() + "_" 
					+ screenshot.getName().substring(15) 
					));
			this.currentError++;
		} catch (IOException e) {
			System.err.println("Impossible to store a screenshot");	
			e.printStackTrace();
		}
	}	

	public void makeErrorPageSource() { 
	    StackTraceElement ste = Thread.currentThread().getStackTrace()[2]; // 2 - уровень отката по стеку вызовов функций
		String source = driver.getPageSource();

		try (PrintWriter out = new PrintWriter( this.pathToScreenshots
				+ flowName + "/"
				+ "err_" + this.currentError + "_"
		        + ste.getClassName() + "_"
		        + ste.getMethodName() + "_" 
		        + ste.getLineNumber() + ".html" )) {
		    out.println(source);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}	

	@Override
	public int getScreenshotHash() {
		byte[] bytes = ( (TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES );
		return java.util.Arrays.hashCode(bytes);
	}
/*	
	@Override
	public int getExpectedHtmlHash() {
		return Integer.parseInt( flowMap.get(this.currentSlideNumber).get("hash") );
	}
	
	private String getCurrentCssSelector() {
		return flowMap.get(this.currentSlideNumber).get("csss");
	}
	
	public boolean checkHtmlHash() {
		
		int hash = this.driver.findElement(By.cssSelector(getCurrentCssSelector()))
		.getAttribute("innerHTML").replaceAll("\\d+|src=.+?\\\"", "").hashCode();
		int expectedHash = getExpectedHtmlHash();
		if (hash != expectedHash) {
			WARNING.writeln("Slide number: " + this.currentSlideNumber + ". Expected hash: " + expectedHash + ". Real hash: " + hash);
			makeScreenshot();
		}
		this.currentSlideNumber++;
		return hash == expectedHash;
	}
*//*
	@Override
	public void waitForHtmlHash(By by) {
		Element el = new Element(this, by);
		if ( dutycycle > 0 && repetitions > 0 ) { // Ожидание задано
			int expectedHash = getExpectedHtmlHash();
		// Цикл ожидания события (изменения состояния)
			int num = 0;
			try {
				
				int hash = el.getHtmlHash();
				while ( ( expectedHash != hash ) && num < repetitions ) {
					Thread.sleep(dutycycle);
					hash = el.getHtmlHash();
					num++;
				}
				
				DEBUG.writeln("Wait for html hash id: " + this.currentSlideNumber + ". Times: " + num + ". Expected hash: " + expectedHash + ". " + by);

				if (num == repetitions) {
					WARNING.writeln("Slide " + this.currentSlideNumber + ". Html hash : " + hash );
					makeScreenshot(); // Недождались необходимого скриншота. Сохраняем такой какой есть
//					throw new ScreenshotTimeoutException();
				}
			} catch (InterruptedException e) {
				makeScreenshot();
				e.printStackTrace();
			}
		}
	}
*/
	@Override
	public void waitForStableLocation(By by, int firstWait) {
//		System.out.println(">> " + by);
		waitForStableLocation(driver.findElement(by), firstWait);
	}
	
	@Override
	public void waitForStableLocation(WebElement el, int firstWait) {
		int a = firstWait;
		int b = a/2;
		int c;
		Point loc1;
		Point loc2 = el.getLocation();
		sleep(a);
		Point loc3 = el.getLocation();
		
		int i = 0;
		do {
			sleep(b);
//			System.out.println(">>> " + i + " >>> b=" + b);
			loc1 = loc2;
			loc2 = loc3;
			loc3 = el.getLocation();
			c = b;
			b += a;
			a = c;
		} while ( (( loc1.getX() != loc2.getX() )
				|| ( loc2.getX() != loc3.getX() )
				|| ( loc1.getY() != loc2.getY() )
				|| ( loc2.getY() != loc3.getY() ))
				&& (++i < 10) );
		if ( i == 10 )
			WARNING.writeln("WebElement location isn't stable. WebElement: " + el.toString() );
	}
	
}

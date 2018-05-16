package agency.july.flow;

import static agency.july.logger.Logevent.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import agency.july.config.models.Accesses;
import agency.july.config.models.Configuration;
import agency.july.webelements.Element;

public class Flow implements IFlow {
	
	private WebDriver driver;
	private String flowName;
	private String pathToScreenshots;
	private List< Map < String, String > > flowMap;
	private int currentSlide = 0;
	
	private int dutycycle = 500;
	private int repetitions = 16;
	
	public Flow (String flowName) {
		this.flowName = flowName;
		this.pathToScreenshots = Accesses.getPathto().get("screenshots") + Configuration.getBrowser().toString() + "/";
		this.flowMap = Configuration.getFlowsss().get(this.flowName);
	}
	
	public String getFlowName() {
		return flowName;
	}

	public int getCurrentSlide() {
		return currentSlide;
	}

	public int nextHashBefore() {
		return Integer.parseInt( flowMap.get(currentSlide).get("hash") );
	}
	
	public void incSlideNumber() {
		currentSlide++;
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
					+ "id_" + this.currentSlide 
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
	public int getScreenshotHash() {
		byte[] bytes = ( (TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES );
		return java.util.Arrays.hashCode(bytes);
	}
	
	@Override
	public int getExpectedHtmlHash() {
		return Integer.parseInt( flowMap.get(this.currentSlide).get("hash") );
	}

/*
	@Override
	public void waitForHtmlHash() {
		String cssSelector = flowMap.get(this.currentSlide).get("csss");
		Element el = new Element(this, By.cssSelector( cssSelector ));
		if ( dutycycle > 0 && repetitions > 0 ) { // Ожидание задано
			int expectedHash = Integer.parseInt( flowMap.get(this.currentSlide).get("hash") );
		// Цикл ожидания события (изменения состояния)
			int num = 0;
			try {
				
				int hash = el.getHtmlHash();
				while ( ( expectedHash != hash ) && num < repetitions ) {
					Thread.sleep(dutycycle);
					hash = el.getHtmlHash();
					num++;
				}
				
				DEBUG.writeln("Wait for html hash id: " + this.currentSlide + ". Times: " + num + ". Hash: " + hash + ". Csss: " + cssSelector);

				if (num == repetitions) {
					WARNING.writeln("Slide " + this.currentSlide + ". Html hash : " + hash );
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
				
				DEBUG.writeln("Wait for html hash id: " + this.currentSlide + ". Times: " + num + ". Expected hash: " + expectedHash + ". " + by);

				if (num == repetitions) {
					WARNING.writeln("Slide " + this.currentSlide + ". Html hash : " + hash );
					makeScreenshot(); // Недождались необходимого скриншота. Сохраняем такой какой есть
//					throw new ScreenshotTimeoutException();
				}
			} catch (InterruptedException e) {
				makeScreenshot();
				e.printStackTrace();
			}
		}
	}
	
}

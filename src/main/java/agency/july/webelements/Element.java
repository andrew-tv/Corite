package agency.july.webelements;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import agency.july.flow.IFlow;

public class Element {
	
	protected IFlow parentFlow;
	protected By by;
	protected By bytoWait;
	protected WebElement el;
	
	protected static int dutycycle = 500;
	protected static int repetitions = 16;
	
	public Element (IFlow parentFlow, By by) {
		this.parentFlow = parentFlow;
		this.by = by;
		this.bytoWait = null;
	}
	
	public Element (IFlow parentFlow, By by, By bytoWait) {
		this.parentFlow = parentFlow;
		this.by = by;
		this.bytoWait = bytoWait;
	}
	
	public WebElement getEl() {
		refresh();
		return el;
	}

	public By getBy() {
		return by;
	}

	public int getHtmlHash() {
		refresh();
		return el.getAttribute("innerHTML").replaceAll("\\d+|src=.+?\\\"", "").hashCode();
	}

	void refresh () {
		WebDriverWait wait = new WebDriverWait(parentFlow.getDriver(), 5);
		wait.until(ExpectedConditions.presenceOfElementLocated(by));
		el = parentFlow.getDriver().findElement(by);
	}
	
	public boolean exists() {
		try {
			if ( this.bytoWait != null ) parentFlow.waitForHtmlHash(this.bytoWait);
			parentFlow.getDriver().findElement(by);
		} catch (NoSuchElementException e) {
			return false;
		}
		return true;
	}
	
	public String getText () {
		if ( this.bytoWait != null ) parentFlow.waitForHtmlHash(this.bytoWait);
		refresh();
		if ( this.bytoWait != null ) parentFlow.incSlideNumber();				
		return this.el.getText();
	}
	
	public void click () {

		WebDriverWait wait = new WebDriverWait(parentFlow.getDriver(), 5);
		if ( this.bytoWait != null ) parentFlow.waitForHtmlHash(this.bytoWait);

		refresh();
		
		wait.until( ExpectedConditions.elementToBeClickable(by) );
		this.el.click();
		
		if ( this.bytoWait != null ) parentFlow.incSlideNumber();				
	}

	public boolean checkAttribute(String attr, String value) {
//		refresh();
		return value.equals( this.el.getAttribute(attr) );
	}
	
}

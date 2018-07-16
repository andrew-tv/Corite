package agency.july.webelements;

import static agency.july.logger.Logevent.*;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import agency.july.flow.IFlow;

public class Element {
	
	protected IFlow parentFlow;
	protected By by;
	protected By bytoWait;
	protected WebElement el;
	
	protected int firstWait = 80;
	
	public Element (IFlow parentFlow, By by) {
		this.parentFlow = parentFlow;
		this.by = by;
		this.bytoWait = null;
	}
	
	public void setFirstWait(int firstWait) {
		this.firstWait = firstWait;
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

	protected void refresh () {
		WebDriverWait wait = new WebDriverWait(parentFlow.getDriver(), 5);
		wait.until(ExpectedConditions.presenceOfElementLocated(by));
		
		parentFlow.waitForStableLocation(by, this.firstWait);
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
//		if ( this.bytoWait != null ) parentFlow.incSlideNumber();				
		return this.el.getText();
	}
	
	public void click () {

		WebDriverWait wait = new WebDriverWait(parentFlow.getDriver(), 5);
		if ( this.bytoWait != null ) parentFlow.waitForHtmlHash(this.bytoWait);

		wait.until( ExpectedConditions.elementToBeClickable(by) );
		
		refresh();
		this.el.click();
		
//		if ( this.bytoWait != null ) parentFlow.incSlideNumber();				
	}

	public String getAttr(String attr) {
		return getEl().getAttribute(attr);
	}

}

package agency.july.webelements;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import agency.july.flow.IFlow;

public class Slider extends Element {

	public Slider(IFlow parentFlow, By by) {
		super(parentFlow, by);
		// TODO Auto-generated constructor stub
	}
/*
	public Slider(IFlow parentFlow, By by, By bytoWait) {
		super(parentFlow, by, bytoWait);
		// TODO Auto-generated constructor stub
	}
*/	
	public void set(int percentPosition) {
		
		if ( this.bytoWait != null ) parentFlow.waitForHtmlHash(this.bytoWait);

		refresh();
		
		int width = el.getSize().getWidth();
	    Actions move = new Actions(parentFlow.getDriver());
	    move.moveToElement(el, ((width * percentPosition)/100), 0).click();
	    move.build().perform();
	}

}

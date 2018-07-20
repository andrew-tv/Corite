package agency.july.webelements;

import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;

import agency.july.flow.IFlow;

public class Slider extends Element {

	public Slider(IFlow parentFlow, By by) {
		super(parentFlow, by);
	}

	public void set(int percentPosition) {
		
		refresh();

		int width = el.getSize().getWidth();
	    Actions move = new Actions(parentFlow.getDriver());
	    move.moveToElement(el, ((width * percentPosition)/100)-(percentPosition < 100 ? 0 : 1), 0).click();
	    move.build().perform();
	}

}

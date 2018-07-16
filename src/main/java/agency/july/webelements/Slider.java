package agency.july.webelements;

import static agency.july.logger.Logevent.*;

import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;

import agency.july.flow.IFlow;

public class Slider extends Element {

	public Slider(IFlow parentFlow, By by) {
		super(parentFlow, by);
	}

	public void set(int percentPosition) {
		
//		if ( this.bytoWait != null ) parentFlow.waitForHtmlHash(this.bytoWait);

		refresh();

		int width = el.getSize().getWidth();
//		DEBUG.writeln("Width of slider: " + width);
//		parentFlow.makeScreenshot("A_" + percentPosition);
	    Actions move = new Actions(parentFlow.getDriver());
	    move.moveToElement(el, ((width * percentPosition)/100)-(percentPosition < 100 ? 0 : 1), 0).click();
	    move.build().perform();
/*		parentFlow.makeScreenshot("B_" + percentPosition);
		parentFlow.sleep(1000);
		parentFlow.makeScreenshot("C_" + percentPosition);
*/	}

}

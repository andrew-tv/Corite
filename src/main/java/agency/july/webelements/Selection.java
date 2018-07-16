package agency.july.webelements;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;

import agency.july.flow.IFlow;

public class Selection extends Element {

	public Selection(IFlow parentFlow, By by) {
		super(parentFlow, by);
		// TODO Auto-generated constructor stub
	}
/*
	public Selection(IFlow parentFlow, By by, By bytoWait) {
		super(parentFlow, by, bytoWait);
		// TODO Auto-generated constructor stub
	}
*/	
	// Choice from dropdown list
	public void set(String value) {

//		if ( this.bytoWait != null ) parentFlow.waitForHtmlHash(this.bytoWait);

		refresh();
		
		Select sel = new Select(el);
		sel.selectByValue(value);
		
//		if ( this.bytoWait != null ) parentFlow.incSlideNumber();				
	}

}

package agency.july.webelements;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import agency.july.flow.Flow;
import agency.july.flow.IFlow;

public class TextInput extends Element {
		
	public TextInput(IFlow parentPage, By by) {
		super(parentPage, by);
	}

	public TextInput(Flow parentPage, By by, By bytoWait) {
		super(parentPage, by, bytoWait);
	}

	// Ввод текста в строку ввода
	public void set(String value) {

		if ( this.bytoWait != null ) parentFlow.waitForHtmlHash(this.bytoWait);

		refresh();
		el.clear();
		el.sendKeys(value); // Ввели текст
		
		if ( this.bytoWait != null ) parentFlow.incSlideNumber();				
	}

	public void set(Keys key) {
		
		if ( this.bytoWait != null ) parentFlow.waitForHtmlHash(this.bytoWait);

		refresh();
		el.sendKeys(key);
	}
}

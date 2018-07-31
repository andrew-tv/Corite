package agency.july.flow;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import agency.july.config.models.Configuration;
import agency.july.webelements.Element;
import agency.july.webelements.TextInput;

public class Swish implements IBankomat {
	
	private String number;
	
	public Swish (String number) {
		this.number = number;
	}

	public String getNumber() {
		return number;
	}

	@Override
	public void pay(Flow flow) {
		Element anotherSwishCardBtn = new Element(flow, By.cssSelector(Configuration.getCsss().get("investpage").get("anotherSwishCardBtn")));
		Element newSwishCardBtn = new Element(flow, By.cssSelector(Configuration.getCsss().get("investpage").get("newSwishCardBtn")));
		TextInput swishPhoneNumber = new TextInput(flow, By.cssSelector(Configuration.getCsss().get("investpage").get("swishPhoneNumber")));
//		Element procedPaymentBtn = new Element(flow, By.cssSelector(Configuration.getCsss().get("commonelements").get("accentBtn")));
		
		boolean userHavePhone = anotherSwishCardBtn.exists(); // User have creditcard
		if ( userHavePhone ) 
			anotherSwishCardBtn.click();
		else
			newSwishCardBtn.click();
			
		swishPhoneNumber.set(number);
		
		flow.makeScreenshot("Pay_A");
		
		flow.getDriver().findElement(By.tagName("body")).click();
		
		flow.makeScreenshot("Pay_B");
		flow.sleep(1000);
		flow.makeScreenshot("Pay_C");
	}

}

package agency.july.flow;

import org.openqa.selenium.By;
import agency.july.config.models.Configuration;
import agency.july.webelements.Element;
import agency.july.webelements.TextInput;

public class Swish implements IBankomat {
	
	private String phone;
	
	public Swish (String phone) {
		this.phone = phone;
	}

	@Override
	public void pay(Flow flow) {
		Element anotherSwishCardBtn = new Element(flow, By.cssSelector(Configuration.getCsss().get("investpage").get("anotherSwishCardBtn")));
		Element newSwishCardBtn = new Element(flow, By.cssSelector(Configuration.getCsss().get("investpage").get("newSwishCardBtn")));
		TextInput swishPhoneNumber = new TextInput(flow, By.cssSelector(Configuration.getCsss().get("investpage").get("swishPhoneNumber")));

		boolean userHavePhone = anotherSwishCardBtn.exists(); // User have creditcard
		if ( userHavePhone ) 
			anotherSwishCardBtn.click();
		else
			newSwishCardBtn.click();
			
		swishPhoneNumber.set(phone);
	}

}

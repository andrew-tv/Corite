package agency.july.flow;

import static agency.july.logger.Logevent.FAILED;
import static agency.july.logger.Logevent.PASSED;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import agency.july.config.models.Configuration;
import agency.july.webelements.Element;
import agency.july.webelements.TextInput;

public class Stripe implements IBankomat {
	
	private String number;
	
	public Stripe(String number) {
		this.number = number;
	}

	public String getNumber() {
		return number;
	}

	@Override
	public void pay(Flow flow) {
		
		Element anotherStripeCardBtn = new Element(flow, By.cssSelector(Configuration.getCsss().get("investpage").get("anotherStripeCardBtn")));
		Element newStripeCardBtn = new Element(flow, By.cssSelector(Configuration.getCsss().get("investpage").get("newStripeCardBtn")));
		Element iframe = new Element(flow, By.cssSelector ("ngx-stripe-card iframe"));
		TextInput cardNumber = new TextInput(flow, By.cssSelector ("input[name=cardnumber]"));
		TextInput expDate = new TextInput(flow, By.cssSelector ("input[name=exp-date]"));
		expDate.setFirstWait(280);
		TextInput cvc = new TextInput(flow, By.cssSelector ("input[name=cvc]"));
		TextInput zip = new TextInput(flow, By.cssSelector ("input[name=postal]"));

		boolean userHaveCard = anotherStripeCardBtn.exists(); // User have creditcard
		if ( userHaveCard ) 
			anotherStripeCardBtn.click();
		else 
			newStripeCardBtn.click();
		if ( iframe.exists() ) {
			
			int i = 0;
			WebDriver driver = flow.getDriver();
			do {
				driver.switchTo().defaultContent();
				flow.sleep(100);
				driver.switchTo().frame(driver.findElement(iframe.getBy()));
			} while ( driver.findElements(By.cssSelector("div#root > form")).size() == 0 && i++ < 20 );
			
			try {
				WebDriverWait waitIntoIframe = new WebDriverWait(driver, 5);
				waitIntoIframe.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector("div#root > form")) );
				PASSED.writeln("There is access to 'Stripe' iframe on the invest page");
			} catch (TimeoutException e) {
				FAILED.writeln("There isn't access to 'Stripe' iframe on the invest page");
				flow.makeErrorScreenshot();
				flow.makeErrorPageSource();
				driver.switchTo().frame(0);
				flow.sleep(1000);
			}
			
			cardNumber.set(number);
			expDate.set("424");
			cvc.set("242");
			zip.set("42424");
			
			driver.switchTo().defaultContent();
			
		} else {
			FAILED.writeln("iframe, for setting the credit card information, is not exist");			
			flow.makeErrorScreenshot();
		}
	}

}

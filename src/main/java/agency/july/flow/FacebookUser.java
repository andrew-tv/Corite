package agency.july.flow;

import static agency.july.logger.Logevent.*;

import java.util.Set;

import org.openqa.selenium.By;

import agency.july.config.models.Configuration;
import agency.july.webelements.Element;
import agency.july.webelements.TextInput;

public class FacebookUser extends User {
	
	// Login page
	private Element facebookBtn;
	// Facebook login page
	private TextInput facebookEmailIn;
	private TextInput facebookPasswordIn;
	private Element facebookLoginBtn;

	public FacebookUser(Flow flow) {
		super(flow);
		
		facebookBtn = new TextInput(this.flow, By.cssSelector(Configuration.getCsss().get("loginpage").get("facebookBtn")) );
		facebookEmailIn = new TextInput(this.flow, By.cssSelector (Configuration.getCsss().get("loginpage").get("facebookEmailIn")) );
		facebookPasswordIn = new TextInput(this.flow, By.cssSelector (Configuration.getCsss().get("loginpage").get("facebookPasswordIn")) );
		facebookLoginBtn = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("loginpage").get("facebookLoginBtn")) );

	}
	
	@Override
	protected void fillLoginForm() {
		String windowHandle = driver.getWindowHandle();
		Set<String> beforeWinHandles = driver.getWindowHandles();
		beforeWinHandles.forEach(System.out::println);

		facebookBtn.click();
		
		flow.sleep(1000);
		
		flow.makeScreenshot("F1");
		Set<String> afterWinHandles = driver.getWindowHandles();
		afterWinHandles.forEach(System.out::println);
		
		afterWinHandles.removeAll(beforeWinHandles);
		
		afterWinHandles.forEach(System.out::println);
		
		// Change window
		if ( !afterWinHandles.isEmpty() ) {
			
			afterWinHandles.forEach(driver.switchTo()::window);
			
			System.out.println(userEmail);
			System.out.println(userPasswd);
			
			facebookEmailIn.set(userEmail);
			flow.sleep(500);
			facebookPasswordIn.set(userPasswd);
			flow.sleep(100);
			
			facebookLoginBtn.click();
			
			driver.switchTo().window(windowHandle);
		} else {
			try {
				checkFormFillingError(true);
			} catch (TestFailedException e) {
				FAILED.writeln("Facebook login window did not appear");
				flow.makeErrorScreenshot();
				throw new TestFailedException();
			}
		}
	}

}

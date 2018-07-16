package agency.july.flow;

import static agency.july.logger.Logevent.*;

import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;

import agency.july.config.models.BrowserProps;
import agency.july.config.models.Configuration;
import agency.july.webelements.Element;
import agency.july.webelements.TextInput;

public class GoogleUser extends User {
	
	// Login page
	private Element googleBtn;
	// Google login page
	private TextInput googleEmailIn;
	private TextInput googlePasswordIn;
	private Element googleIdentifierNextBtn;
	private Element googlePasswordNextBtn;

	public GoogleUser(Flow flow, BrowserProps props) {
		super(flow, props);
		
		googleBtn = new TextInput(this.flow, By.cssSelector(Configuration.getCsss().get("loginpage").get("googleBtn")) );
		googleEmailIn = new TextInput(this.flow, By.cssSelector (Configuration.getCsss().get("loginpage").get("googleEmailIn")) );
		googlePasswordIn = new TextInput(this.flow, By.cssSelector (Configuration.getCsss().get("loginpage").get("googlePasswordIn")) );
		googleIdentifierNextBtn = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("loginpage").get("googleIdentifierNextBtn")) );
		googlePasswordNextBtn = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("loginpage").get("googlePasswordNextBtn")) );

	}
	
	@Override
	protected void fillLoginForm() {
		String windowHandle = driver.getWindowHandle();
		Set<String> beforeWinHandles = driver.getWindowHandles();
		beforeWinHandles.forEach(System.out::println);

//		flow.sleep(1000);
		googleBtn.click();
		
		flow.sleep(1000);
		
		Set<String> afterWinHandles = driver.getWindowHandles();
		afterWinHandles.forEach(System.out::println);
		
		afterWinHandles.removeAll(beforeWinHandles);
		
		afterWinHandles.forEach(System.out::println);
		
		// Change window
		if ( !afterWinHandles.isEmpty() ) {
			
			afterWinHandles.forEach(driver.switchTo()::window);
			driver.manage().window().setSize(new Dimension(800, 600) );
			
//			System.out.println(userEmail);
//			System.out.println(userPasswd);
			
			googleEmailIn.set(userEmail);
			googleIdentifierNextBtn.click();
			flow.sleep(1000);
			googlePasswordIn.set(userPasswd);
//			flow.sleep(100);
			googlePasswordNextBtn.click();
			
			driver.switchTo().window(windowHandle);
		} else {
			try {
				checkFormFillingError(true);
			} catch (TestFailedException e) {
				FAILED.writeln("Google login window did not appear");
				flow.makeErrorScreenshot();
				throw new TestFailedException();
			}
		}

	}

}

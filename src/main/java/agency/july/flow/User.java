package agency.july.flow;

import static agency.july.logger.Logevent.*;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import agency.july.config.models.Accesses;
import agency.july.config.models.Configuration;
import agency.july.sendmail.ImapClient;
import agency.july.webelements.Element;
import agency.july.webelements.TextInput;

public class User extends Test {
	
	protected String userEmail = "";
	protected String userFullName = "";
	protected String userFirstName = "";
	protected String userLastName = "";
	protected String userPasswd = "";
	protected String userTel = "";
	
	// Explore page
	private Element loginBtn;
	protected Element profileBtn;
	private Element menuMyCampaign;
	private Element menuLogoutBtn;
	private Element userNameTxt;
	
	// Login page
	private TextInput emailIn;
	private TextInput passwordIn;
	private Element submitBtn;

	// Register page
	private TextInput yourEmailIn;
	private TextInput firstNameIn;
	private TextInput lastNameIn;
	private TextInput passwordNewIn;
	private TextInput passwordConfirmIn;
	private TextInput telephoneIn;
	private Element registerSubmit;
	private Element startCampaignTab;
	
	// Start Campaign page
	private TextInput campaignImage;
	private TextInput campaignSong;
	private TextInput campaignName;
	private TextInput campaignArtist;
	private TextInput campaignGenre;
	private Element campaignGenreSelect;
	private TextInput campaignValue;
	private TextInput textArea;
	private Element iagree;	
	private Element campaignPublish;
	
	public User(Flow flow) {
		super(flow);
	    goHome();
		
		// Explore page
		loginBtn =	new Element(this.flow, By.cssSelector(Configuration.getCsss().get("explorepage").get("loginBtn")), By.cssSelector("nav.top"));
		profileBtn = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("explorepage").get("profileBtn")), By.cssSelector("nav.top"));
		menuMyCampaign = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("explorepage").get("menuMyCampaign")));
		menuLogoutBtn = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("explorepage").get("menuLogoutBtn")), By.cssSelector(".mat-menu-content") );
		userNameTxt = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("explorepage").get("userName")), By.cssSelector("nav.top") );
		startCampaignTab = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("explorepage").get("startCampaignTab")));
		// Login page
		emailIn = new TextInput(this.flow, By.cssSelector (Configuration.getCsss().get("loginpage").get("email")), By.cssSelector("page-account-login"));
		passwordIn = new TextInput(this.flow, By.cssSelector(Configuration.getCsss().get("loginpage").get("password")), By.cssSelector("page-account-login"));
		submitBtn = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("loginpage").get("submit")), By.cssSelector("page-account-login"));
		// Register page
		yourEmailIn = new TextInput(this.flow, By.cssSelector(Configuration.getCsss().get("registerpage").get("yourEmailIn")));
		firstNameIn = new TextInput(this.flow, By.cssSelector(Configuration.getCsss().get("registerpage").get("firstNameIn")));
		lastNameIn = new TextInput(this.flow, By.cssSelector(Configuration.getCsss().get("registerpage").get("lastNameIn")));
		passwordNewIn = new TextInput(this.flow, By.cssSelector(Configuration.getCsss().get("registerpage").get("passwordNewIn")));
		passwordConfirmIn = new TextInput(this.flow, By.cssSelector(Configuration.getCsss().get("registerpage").get("passwordConfirmIn")));
		telephoneIn = new TextInput(this.flow, By.cssSelector(Configuration.getCsss().get("registerpage").get("telephoneIn")));
		registerSubmit = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("registerpage").get("registerSubmit")), By.cssSelector("page-account-register"));
	}
	
	public User withUser(String user) {
		this.userEmail = Accesses.getLogins().get(user);
		this.userPasswd = Accesses.getPasswds().get(user);
		this.userFullName = Accesses.getUsernames().get(user);
		String[] fullName = this.userFullName.split(" +");
		this.userFirstName = fullName[0];
		this.userLastName = fullName[1];
		this.userTel = "+380507502818";
		return this;
	}
	
	@Override
    public void goHome() { 
    	driver.get(Accesses.getUrls().get("dev"));
    }

	public String getUserFullName() {
		return userFullName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void login () {
		
		ACTION.writeln("Login user : " + this.userEmail);		
		flow.setDriver(driver);
		WebDriverWait wait = new WebDriverWait(driver, 5);
		
		try {
			wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector("page-explore-list")) );
			PASSED.writeln("Page with element 'page-explore-list' has been reached");
		} catch (TimeoutException e) {
			FAILED.writeln("Page with element 'page-explore-list' has not been reached");
			throw new TestFailedException();
		}
		
		try {
			
			if ( !userNameTxt.exists() ) { // The user is not login
								
				loginBtn.click();
				wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector("page-account-login")) );
				emailIn.set(userEmail);
				submitBtn.click();
				passwordIn.set(userPasswd);
				submitBtn.click();
				
				if ( !userNameTxt.exists() ) throw new TestFailedException();
				
			} else {
				FAILED.writeln("The user has been already loged in, but expected otherwise : " + this.userEmail);
				throw new TestFailedException();
			}
		} catch (TestFailedException e) {
			throw new TestFailedException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new TestFailedException();
		}
	}

	public void logout () {
		
		ACTION.writeln("Logout an user : " + this.userEmail);
		flow.setDriver(driver);
		
		try {
			if ( userNameTxt.exists() ) {
				
				profileBtn.click();
				menuLogoutBtn.click();
				
			} else {
				FAILED.writeln("The user has already been logged out but expected otherwise : " + this.userEmail);
				throw new TestFailedException();
			}
		} catch (TestFailedException e) {
			throw new TestFailedException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new TestFailedException();
		}
	}

	public void register () {
		
		ACTION.writeln("Register an user : " + this.userEmail);
		flow.setDriver(driver);
		driver.get(Accesses.getUrls().get("dev") + "/user/register");
		WebDriverWait wait = new WebDriverWait(driver, 10);
		try {
			wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector("page-account-register")) );
			PASSED.writeln("Registration page has been reached");
		} catch (TimeoutException e1) {
			FAILED.writeln("Registration page has not been reached");
			throw new TestFailedException();
		}


		try {
				
			yourEmailIn.set(userEmail);
			firstNameIn.set(userFirstName);
			lastNameIn.set(userLastName);
			passwordNewIn.set(userPasswd);
			passwordConfirmIn.set(userPasswd);
			telephoneIn.set(userTel);
			registerSubmit.click();
			
			// Check email
			confirmation( getToken(
				Accesses.getLogins().get("noreply"),	// from
				this.userEmail,								// to
				Configuration.getCsss().get("confirmlinks").get("registration") // confirm link in the letter
			));
			
			try {
				wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector("page-account-check")) );
				PASSED.writeln("Thankyou page has been reached");
			} catch (TimeoutException e1) {
				FAILED.writeln("Thankyou page has not been reached");
				throw new TestFailedException();
			}
						
		} catch (TestFailedException e) {
			throw new TestFailedException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new TestFailedException();
		}
	}

	private void confirmation(String tokenURL) {
		if ( tokenURL.isEmpty() ) { // Не получено письмо с токеном подтверждения регистрации
			FAILED.writeln("The token has not been received in the registration confirmation letter or the time was out");
			throw new TestFailedException();
		} else { // Все нормально, подтверждаем регистрацию и проверяем это выходом на thankyou page, где находим имя зарегестрированного юзера
			PASSED.writeln("The token has been received");
			driver.get(tokenURL);
		}
	}

	private String getToken (String sender, String recipient, String selector) throws InterruptedException {
		String tokenURL = "";
		ACTION.writeln("Confirmation registration >> From: " + sender + " To: " + recipient);
		for (int i = 0; i < 5; i++) { // Ожидание письма до 5 раз по 5 с
			Thread.sleep(5000);
		    ImapClient imapClient = new ImapClient (Accesses.getEmail());
		    
			tokenURL = imapClient.getHref( sender, recipient, selector );
			
	    	imapClient.close();
			ACTION.writeln("Waiting for email in \"" + Accesses.getEmail().getFolder() + "\" folder >> " + i + " >> Confirm URL for registration >> " + (tokenURL.isEmpty() ? "???" : tokenURL));
			if ( !tokenURL.isEmpty() ) break;
		}
		return tokenURL;
	}
	
	public void startCampaign () {
		
		campaignImage = new TextInput(this.flow, By.cssSelector(Configuration.getCsss().get("startcampaignpage").get("campaignImage")), By.cssSelector("page-my-campaigns"));
		campaignSong = new TextInput(this.flow, By.cssSelector(Configuration.getCsss().get("startcampaignpage").get("campaignSong")));
		campaignName = new TextInput(this.flow, By.cssSelector(Configuration.getCsss().get("startcampaignpage").get("campaignName")));
		campaignArtist = new TextInput(this.flow, By.cssSelector(Configuration.getCsss().get("startcampaignpage").get("campaignArtist")));
		campaignGenre = new TextInput(this.flow, By.cssSelector(Configuration.getCsss().get("startcampaignpage").get("campaignGenre")));
		campaignGenreSelect = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("startcampaignpage").get("campaignGenreSelect")));
		campaignValue = new TextInput(this.flow, By.cssSelector(Configuration.getCsss().get("startcampaignpage").get("campaignValue")));
		textArea = new TextInput(this.flow, By.cssSelector(Configuration.getCsss().get("startcampaignpage").get("textArea")));
		iagree = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("startcampaignpage").get("iagree")));
		campaignPublish = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("startcampaignpage").get("campaignPublish")));

		
		ACTION.writeln("Start Campaign ");
		flow.setDriver(driver);
		WebDriverWait wait = new WebDriverWait(driver, 50);
		
		try {
				
			startCampaignTab.click(); // Go to start campaign page
			
			flow.incSlideNumber();
			
			campaignImage.set(new File (Accesses.getPathto().get("files") + "Abba-007.jpg").getAbsolutePath());
//			wait.until( ExpectedConditions.attributeToBeNotEmpty(driver.findElement(By.cssSelector("app-img > img")), "src") );

			campaignValue.set(Keys.ARROW_RIGHT);
			campaignValue.set(Keys.ARROW_RIGHT);

			campaignSong.set(new File (Accesses.getPathto().get("files") + "abba__the_day_before_you_came.mp3").getAbsolutePath());
			try {
				wait.until( ExpectedConditions.textToBe(By.cssSelector("div.audio > div > div.slider__max"), "05:49") );
				PASSED.writeln("Audio has been uploaded");
			} catch (TimeoutException e1) {
				FAILED.writeln("Problem with uploading audio. Timeout 50 sec.");
			}
			
			campaignName.set("The day before you came");
			campaignArtist.set("ABBA");
			campaignGenre.set("Pop");
			campaignGenreSelect.click();
			textArea.set("Last ABBA's song");
			iagree.click();

			campaignPublish.click();
			
			wait.withTimeout(5, TimeUnit.SECONDS);
			try {
				wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector("page-campaign-own-edit")) ); //
				PASSED.writeln("Next page with element 'page-campaign-own-edit' has been reached");
			} catch (TimeoutException e) {
				FAILED.writeln("Next page with element 'page-campaign-own-edit' has not been reached");
				throw new TestFailedException();
			}
			
			profileBtn.click();
			menuMyCampaign.click();

			try {
				wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector("page-campaign-own-list")) ); //
				PASSED.writeln("Next page with element 'page-campaign-own-list' has been reached");
			} catch (TimeoutException e) {
				FAILED.writeln("Next page with element 'page-campaign-own-list' has not been reached");
				throw new TestFailedException();
			}
			
		} catch (TestFailedException e) {
			throw new TestFailedException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new TestFailedException();
		}
	}
	

}

package agency.july.flow;

import static agency.july.logger.Logevent.*;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;

import agency.july.config.models.Accesses;
import agency.july.config.models.Configuration;
import agency.july.sendmail.ImapClient;
import agency.july.webelements.Element;
import agency.july.webelements.Slider;
import agency.july.webelements.TextInput;

public class User extends Test {
	
	protected String userEmail = "";
	protected String userFullName = "";
	protected String userFirstName = "";
	protected String userLastName = "";
	protected String userPasswd = "";
	protected String userTel = "";
	
	// Common elements
	private Element matError;
	private Element submitBtn;
	
	// Explore page
	private Element loginBtn;
	protected Element profileBtn;
	private Element menuMyCampaign;
	private Element menuLogoutBtn;
	private Element userNameTxt;
	
	// Explore a Campaign page

	// Invest page
	private Slider slider;
	private Element campaignStatus;
	private Element buyCoritesBtn;
//	private Element nextBtn;
	private Element accentBtn;
	private Element thankyou;
	
	// Login page
	private TextInput emailIn;
	private TextInput passwordIn;
//	private Element submitBtn;

	// Register page
	private TextInput yourEmailIn;
	private TextInput firstNameIn;
	private TextInput lastNameIn;
	private TextInput passwordNewIn;
	private TextInput passwordConfirmIn;
	private TextInput telephoneIn;
//	private Element registerSubmit;
	private Element startCampaignTab;
	
	// Start Campaign page
	private TextInput campaignImage;
	private TextInput campaignSong;
	private TextInput campaignName;
	private TextInput campaignArtist;
	private TextInput campaignGenre;
	private Element campaignGenreSelect;
//	private TextInput campaignValue;
	private TextInput textArea;
	private Element iagree;	
//	private Element campaignPublish;
	
	public User(Flow flow) {
		super(flow);
		
		// Common elements
		matError = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("commonelements").get("matError")));
		submitBtn = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("commonelements").get("submitBtn")) );
		// Explore page
		loginBtn =	new Element(this.flow, By.cssSelector(Configuration.getCsss().get("explorepage").get("loginBtn")) );
		profileBtn = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("explorepage").get("profileBtn")) );
		menuMyCampaign = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("explorepage").get("menuMyCampaign")));
		menuLogoutBtn = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("explorepage").get("menuLogoutBtn")) );
		userNameTxt = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("explorepage").get("userName")) );
		startCampaignTab = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("explorepage").get("startCampaignTab")));
		// Invest page
		slider = new Slider(this.flow, By.cssSelector(Configuration.getCsss().get("investpage").get("slider")));
		campaignStatus = new Slider(this.flow, By.cssSelector(Configuration.getCsss().get("investpage").get("campaignStatus")));
		buyCoritesBtn = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("investpage").get("buyCoritesBtn")));
//		nextBtn = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("investpage").get("nextBtn")));
		accentBtn = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("investpage").get("accentBtn")));
		thankyou = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("investpage").get("thankyou")));
		// Login page
		emailIn = new TextInput(this.flow, By.cssSelector (Configuration.getCsss().get("loginpage").get("email")) );
		passwordIn = new TextInput(this.flow, By.cssSelector(Configuration.getCsss().get("loginpage").get("password")) );
		// Register page
		yourEmailIn = new TextInput(this.flow, By.cssSelector(Configuration.getCsss().get("registerpage").get("yourEmailIn")));
		firstNameIn = new TextInput(this.flow, By.cssSelector(Configuration.getCsss().get("registerpage").get("firstNameIn")));
		lastNameIn = new TextInput(this.flow, By.cssSelector(Configuration.getCsss().get("registerpage").get("lastNameIn")));
		passwordNewIn = new TextInput(this.flow, By.cssSelector(Configuration.getCsss().get("registerpage").get("passwordNewIn")));
		passwordConfirmIn = new TextInput(this.flow, By.cssSelector(Configuration.getCsss().get("registerpage").get("passwordConfirmIn")));
		telephoneIn = new TextInput(this.flow, By.cssSelector(Configuration.getCsss().get("registerpage").get("telephoneIn")));
//		registerSubmit = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("registerpage").get("registerSubmit")));
		// Start Campaign page
//		campaignValue = new TextInput(this.flow, By.cssSelector(Configuration.getCsss().get("startcampaignpage").get("campaignValue")));
	}
	
	public User withUser(String user) {
		this.userEmail = Accesses.getLogins().get(user);
		this.userPasswd = Accesses.getPasswds().get(user);
		this.userFullName = Accesses.getUsernames().get(user);
		String[] fullName = this.userFullName.split(" +");
		this.userFirstName = fullName[0];
		this.userLastName = fullName[1];
		this.userTel = "+3805011111111";
		return this;
	}
/*	
    @Override
	public String getBaseUrl() {
		return Accesses.getUrls().get("base");
	}
*/	
	public String getUserFullName() {
		return userFullName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void login () {
		
		ACTION.writeln("Login user : " + this.userEmail);		
		flow.setDriver(driver);
		WebDriverWait wait = new WebDriverWait(driver, 15);
		
	    goHome();
		try {
			wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector("page-explore-list")) );
			PASSED.writeln("Page with element 'page-explore-list' has been reached");
		} catch (TimeoutException e) {
			FAILED.writeln("Page with element 'page-explore-list' has not been reached");
			throw new TestFailedException();
		}
		
		try {
			
			if ( loginBtn.exists() ) { // The user is not login
								
				loginBtn.click();
				try {
					wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector("page-account-login")) );
					PASSED.writeln("Page with element 'page-account-login' was reached");
				} catch (TimeoutException e) {
					FAILED.writeln("Page with element 'page-account-login' was't reached");
					throw new TestFailedException();
				}
				emailIn.set(userEmail);
				submitBtn.click();
				checkFormFillingError();
				passwordIn.set(userPasswd);
				submitBtn.click();
				
//				wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("page-explore-list")));

				if ( !userNameTxt.exists() ) checkFormFillingError();
				PASSED.writeln("User '" + userNameTxt.getText() + "' logged in");
				
			} else {
				if ( userNameTxt.exists() ) {
					WARNING.writeln("The user has been already logged in, but expected otherwise : " + this.userEmail 
							+ ". User name: " + userNameTxt.getText());
//					flow.makeScreenshot();
//					throw new TestFailedException();
				}
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
			
			Thread.sleep(1000); // Без этой задержки не работает. Почему???
			submitBtn.click();

			checkFormFillingError();
			// Check email
			try {
				driver.get( getToken(
					Accesses.getLogins().get("noreply"),	// from
					this.getUserEmail(),								// to
					Configuration.getCsss().get("confirmlinks").get("registration") // confirm link in the letter
				));
			} catch (TestFailedException e) {
				FAILED.writeln("User registration was failed. User email: " + this.getUserEmail() );
				throw new TestFailedException();				
			} 
			
			try {
				wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector("page-account-check")) );
				PASSED.writeln("Thankyou page of registration has been reached");
			} catch (TimeoutException e1) {
				FAILED.writeln("Thankyou page  of registration has not been reached");
//				throw new TestFailedException();
			}
			
						
		} catch (TestFailedException e) {
			throw new TestFailedException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new TestFailedException();
		}
	}

	protected String getToken (String sender, String recipient, String selector) throws InterruptedException {
		String tokenURL = "";
		ACTION.writeln("Waiting for email in \"" + Accesses.getEmail().getFolder() + "\" folder >> ");
		for (int i = 0; i < 5; i++) { // Ожидание письма до 5 раз по 5 с
			Thread.sleep(5000);
		    ImapClient imapClient = new ImapClient (Accesses.getEmail());
		    
			tokenURL = imapClient.getHref( sender, recipient, selector );
			
	    	imapClient.close();
			if ( !tokenURL.isEmpty() ) break;
		}
		if ( tokenURL.isEmpty() ) { // Не получено письмо с токеном подтверждения регистрации
			FAILED.writeln("The token has not been received in the email or the time was out." 
					+ "\n\tSender: " + sender
					+ "\n\tRecipient: " + recipient
					+ "\n\tCss selector of confirm link: " + selector);
			throw new TestFailedException();
		} else {
			PASSED.writeln("The token has been received: " + tokenURL);
		}
		return tokenURL;
	}
	
	public void startCampaign () {
		
		campaignImage = new TextInput(this.flow, By.cssSelector(Configuration.getCsss().get("startcampaignpage").get("campaignImage"))/*, By.cssSelector("page-my-campaigns")*/);
		campaignSong = new TextInput(this.flow, By.cssSelector(Configuration.getCsss().get("startcampaignpage").get("campaignSong")));
		campaignName = new TextInput(this.flow, By.cssSelector(Configuration.getCsss().get("startcampaignpage").get("campaignName")));
		campaignArtist = new TextInput(this.flow, By.cssSelector(Configuration.getCsss().get("startcampaignpage").get("campaignArtist")));
		campaignGenre = new TextInput(this.flow, By.cssSelector(Configuration.getCsss().get("startcampaignpage").get("campaignGenre")));
		campaignGenreSelect = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("startcampaignpage").get("campaignGenreSelect")));
		textArea = new TextInput(this.flow, By.cssSelector(Configuration.getCsss().get("startcampaignpage").get("textArea")));
		iagree = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("startcampaignpage").get("iagree")));
//		campaignPublish = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("startcampaignpage").get("campaignPublish")));

		
		ACTION.writeln("Start Campaign ");
		flow.setDriver(driver);
		WebDriverWait wait = new WebDriverWait(driver, 50);
		
		try {
				
			startCampaignTab.click(); // Go to start campaign page
			
			campaignImage.set(new File (Accesses.getPathto().get("files") + "Abba-007.jpg").getAbsolutePath());
//			campaignValue.set(Keys.ARROW_RIGHT);			
			slider.set(20);
			
//			campaignSong.set(new File (Accesses.getPathto().get("files") + "abba__the_day_before_you_came.mp3").getAbsolutePath());
			campaignSong.set(new File (Accesses.getPathto().get("files") + "chillingmusic.wav").getAbsolutePath());
			try {
				wait.until( ExpectedConditions.textToBe(By.cssSelector("div.audio > div > div.slider__max"), "00:27") );
				PASSED.writeln("Audio has been uploaded");
			} catch (TimeoutException e1) {
				FAILED.writeln("Problem with uploading audio. Timeout 50 sec.");
			}
			
			campaignName.set(Configuration.getPatterns().get(1));
			campaignArtist.set("ABBA");
			campaignGenre.set("Pop");
			campaignGenreSelect.click();
			textArea.set("Last ABBA's song");
			iagree.click();

			wait.withTimeout(5, TimeUnit.SECONDS);
			
			submitBtn.click();
			wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector(".mat-flat-button.mat-primary.ng-star-inserted")) ); //
			submitBtn.click();
			
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
	
	public void buyCorites (String campaignId) {
		
		TextInput cardNumber = new TextInput(this.flow, By.cssSelector ("input[name=cardnumber]"));
		TextInput expDate = new TextInput(this.flow, By.cssSelector ("input[name=exp-date]"));
		TextInput cvc = new TextInput(this.flow, By.cssSelector ("input[name=cvc]"));
		TextInput zip = new TextInput(this.flow, By.cssSelector ("input[name=postal]"));
		Element iframe = new Element(this.flow, By.cssSelector ("ngx-stripe-card iframe"));
		
		ACTION.writeln("Buy corites");
		flow.setDriver(driver);
		WebDriverWait wait = new WebDriverWait(driver, 5);

		// Go to campaign page
		driver.get(getBaseUrl() + "/explore/" + campaignId);
		try {
			wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector("page-explore-view")) );
			PASSED.writeln("Page with element 'page-explore-view' has been reached");
		} catch (TimeoutException e) {
			FAILED.writeln("Page with element 'page-explore-view' has not been reached");
			flow.makeErrorScreenshot();
//			throw new TestFailedException();
		}
		
		// Check if the campaign is active
		if ( campaignStatus.getText().equals("ACTIVE") ) {
			PASSED.writeln("Campaign is active");
		} else {
			FAILED.writeln("Campaign is not active. Expected – active");
			flow.makeErrorScreenshot();
//			throw new TestFailedException();
		}
		
		buyCoritesBtn.click();
		
		// Go to invest page
		try {
			wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector("page-explore-invest")) );
			PASSED.writeln("Page with element 'page-explore-invest' has been reached");
		} catch (TimeoutException e) {
			FAILED.writeln("Page with element 'page-explore-invest' has not been reached");
			flow.makeErrorScreenshot();
//			throw new TestFailedException();
		}
				
		slider.set(100); // Buy all corites
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		boolean userHaveCard = accentBtn.exists(); // User have creditcard
		
		flow.makeScreenshot("A");
		
//		DEBUG.writeln("User have credit card: " + userHaveCard);
		if ( userHaveCard ) 
			accentBtn.click();
		else
			submitBtn.click();
		
		if ( iframe.exists() ) {
		
			driver.switchTo().frame(iframe.getEl());
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			cardNumber.set("4242424242424242");
			expDate.set("424");
			cvc.set("242");
			zip.set("42424");
			
			driver.switchTo().defaultContent();
			
			if ( userHaveCard ) {
				submitBtn.click(); // Get corites
				
				if ( thankyou.exists() ) {
				
					driver.get(getBaseUrl() + "/explore/" + campaignId);
					
					// Check if the campaign is funded
					if ( campaignStatus.exists() && campaignStatus.getText().equals("FUNDED") ) {
						PASSED.writeln("Campaign is funded. Campaign Id: " + campaignId);
					} else {
						FAILED.writeln("Campaign is '" + campaignStatus.getText() + "'. Expected – funded. Campaign Id: " + campaignId);
						flow.makeErrorScreenshot();
			//			throw new TestFailedException();
					}
				} else {
					FAILED.writeln("Thankyou invest page was not reached. See screenshot: 'Error_buyCorites_1'");
					flow.makeErrorScreenshot();
				}
			} else
				accentBtn.click(); // Cancel getting corites
			
		} else {
			FAILED.writeln("iframe for credit card information is not exist. See screenshot: 'Error_buyCorites_2'");			
			flow.makeErrorScreenshot();
		}
	}
	
	public void buyCoritesIncorrectCard (String campaignId, String cardNumber) {
		
		TextInput cardNumberIn = new TextInput(this.flow, By.cssSelector ("input[name=cardnumber]"));
		TextInput expDate = new TextInput(this.flow, By.cssSelector ("input[name=exp-date]"));
		TextInput cvc = new TextInput(this.flow, By.cssSelector ("input[name=cvc]"));
		TextInput zip = new TextInput(this.flow, By.cssSelector ("input[name=postal]"));
		Element iframe = new Element(this.flow, By.cssSelector ("ngx-stripe-card iframe"));
		
		ACTION.writeln("Buy corites with card #" + cardNumber);
		flow.setDriver(driver);
		WebDriverWait wait = new WebDriverWait(driver, 5);

		// Go to campaign page
		driver.get(getBaseUrl() + "/explore/" + campaignId);
		try {
			wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector("page-explore-view")) );
			PASSED.writeln("Page with element 'page-explore-view' has been reached");
		} catch (TimeoutException e) {
			FAILED.writeln("Page with element 'page-explore-view' has not been reached");
			flow.makeErrorScreenshot();
//			throw new TestFailedException();
		}
		
		// Check if the campaign is active
		if ( campaignStatus.getText().equals("ACTIVE") ) {
			PASSED.writeln("Campaign is active");
		} else {
			FAILED.writeln("Campaign is not active. Expected – active");
			flow.makeErrorScreenshot();
//			throw new TestFailedException();
		}
		
		buyCoritesBtn.click();
		
		// Go to invest page
		try {
			wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector("page-explore-invest")) );
			PASSED.writeln("Page with element 'page-explore-invest' has been reached");
		} catch (TimeoutException e) {
			FAILED.writeln("Page with element 'page-explore-invest' has not been reached");
			flow.makeErrorScreenshot();
//			throw new TestFailedException();
		}
				
		slider.set(50); // Buy a half of corites
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		boolean userHaveCard = accentBtn.exists(); // User have creditcard
		
		flow.makeScreenshot("A");
		
		if ( userHaveCard ) 
			accentBtn.click();
		else
			submitBtn.click();
		
		if ( iframe.exists() ) {
		
			driver.switchTo().frame(iframe.getEl());
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			cardNumberIn.set(cardNumber);
			expDate.set("424");
			cvc.set("242");
			zip.set("42424");
			
			driver.switchTo().defaultContent();
			
			try {
				wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector("mat-error")) );
				PASSED.writeln("Incorrect card was declined with message: '" + matError.getText() + "'");
			} catch (TimeoutException e) {
				FAILED.writeln("Incorrect card was not declined");
				flow.makeErrorScreenshot();
//				throw new TestFailedException();
			}			
			
			flow.makeScreenshot("B");
/*			if ( userHaveCard ) {
				nextBtn.click(); // Get corites
				
				if ( thankyou.exists() ) {
				
					driver.get(getBaseUrl() + "/explore/" + campaignId);
					
					// Check if the campaign is funded
					if ( campaignStatus.exists() && campaignStatus.getText().equals("FUNDED") ) {
						PASSED.writeln("Campaign is funded. Campaign Id: " + campaignId);
					} else {
						FAILED.writeln("Campaign is '" + campaignStatus.getText() + "'. Expected – funded. Campaign Id: " + campaignId);
						flow.makeErrorScreenshot();
			//			throw new TestFailedException();
					}
				} else {
					FAILED.writeln("Thankyou invest page was not reached. See screenshot: 'Error_buyCorites_1'");
					flow.makeErrorScreenshot();
				}
			} else
*/				accentBtn.click(); // Cancel getting corites
			
		} else {
			FAILED.writeln("iframe for credit card information is not exist");			
			flow.makeErrorScreenshot();
		}
	}
	
    public void teardown () {
    	try {
    		driver.get(getBaseUrl() + "/logout");
    	} catch (org.openqa.selenium.UnhandledAlertException e) {
			FAILED.writeln("User logout error. User email '" + this.getUserEmail() + "'");			
			flow.makeErrorScreenshot();    		
    	}
    	driver.quit();
    }

	public void fillReleaseInfo(String campaignId) {
		
 		TextInput songName = new TextInput(this.flow, By.cssSelector (Configuration.getCsss().get("releaseinfopage").get("songName")));
		TextInput cover = new TextInput(this.flow, By.cssSelector (Configuration.getCsss().get("releaseinfopage").get("cover")));
 		TextInput label = new TextInput(this.flow, By.cssSelector (Configuration.getCsss().get("releaseinfopage").get("label")));
	    TextInput company = new TextInput(this.flow, By.cssSelector (Configuration.getCsss().get("releaseinfopage").get("company")));
	    TextInput isrc = new TextInput(this.flow, By.cssSelector (Configuration.getCsss().get("releaseinfopage").get("isrc")));
	    TextInput artists = new TextInput(this.flow, By.cssSelector (Configuration.getCsss().get("releaseinfopage").get("artists")));
	    TextInput producers = new TextInput(this.flow, By.cssSelector (Configuration.getCsss().get("releaseinfopage").get("producers")));
	    TextInput composers = new TextInput(this.flow, By.cssSelector (Configuration.getCsss().get("releaseinfopage").get("composers")));
	    TextInput mixers = new TextInput(this.flow, By.cssSelector (Configuration.getCsss().get("releaseinfopage").get("mixers")));
	    TextInput publishers = new TextInput(this.flow, By.cssSelector (Configuration.getCsss().get("releaseinfopage").get("publishers")));
	    TextInput countries = new TextInput(this.flow, By.cssSelector (Configuration.getCsss().get("releaseinfopage").get("countries")));
	    Element country = new Element(this.flow, By.cssSelector (Configuration.getCsss().get("releaseinfopage").get("country")));
	    TextInput date = new TextInput(this.flow, By.cssSelector (Configuration.getCsss().get("releaseinfopage").get("date")));
	    Element releaseBtn = new Element(this.flow, By.cssSelector (Configuration.getCsss().get("releaseinfopage").get("releaseBtn")));

		ACTION.writeln("Filling in release information. Campaign Id: " + campaignId);
		flow.setDriver(driver);
		WebDriverWait wait = new WebDriverWait(driver, 5);

		driver.get(this.getBaseUrl() + "/my-campaigns/" + campaignId + "/release");
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("page-campaign-own-release")));
		
		songName.set("The day before you came");
		cover.set(new File (Accesses.getPathto().get("files") + "ABBA_-_Super_Trouper_(Polar).jpg").getAbsolutePath());
		
		label.set("Polar, Polydor, Atlantic, Epic, RCA Victor, Vogue, Sunshine");
		company.set("Polar Music Studio");
		Random random =  new Random();
		Integer isrcNum = random.nextInt(1000000 - 100000) + 100000;
		String isrcStr = isrcNum.toString();
		isrc.set(isrcStr);
		artists.set("Agnetha Fältskog, Björn Ulvaeus, Benny Andersson, Anni-Frid Lyngstad");
		producers.set("Stig Anderson");
		composers.set("Benny Andersson");
		mixers.set("Benny Andersson");
		publishers.set("Sony BMG Music Entertainment");
		countries.click();
		country.click();
		releaseBtn.click();
				
//		((JavascriptExecutor)driver).executeScript("scroll(0,400)");
	
	}

	public void checkDeclineEmail() {
		flow.setDriver(driver);
		try {
			driver.get( getToken(
				Accesses.getLogins().get("noreply"),	// from
				this.getUserEmail(),								// to
				Configuration.getCsss().get("confirmlinks").get("declined") // confirm link in the letter
			));

			flow.checkHtmlHash();
			
		} catch (TestFailedException | InterruptedException e) {
			FAILED.writeln("Problem with receiving an email with campaign declining message. User email: " + this.getUserEmail() );
			throw new TestFailedException();				
		}
		
		
	}

}

package agency.july.flow;

import static agency.july.logger.Logevent.*;

import java.io.File;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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
	private Element accentBtn;
	
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
	private Element thankyou;
	private Element iframe;
	
	// Login page
	private TextInput emailIn;
	private TextInput passwordIn;

	// Register page
	private TextInput yourEmailIn;
	private TextInput firstNameIn;
	private TextInput lastNameIn;
	private TextInput passwordNewIn;
	private TextInput passwordConfirmIn;
	private TextInput telephoneIn;
	private Element startCampaignTab;
	
	// Start Campaign page
	private TextInput campaignImage;
	private TextInput campaignSong;
	private TextInput campaignName;
	private TextInput campaignArtist;
	private TextInput campaignGenre;
	private Element campaignGenreSelect;
	private TextInput textArea;
	private Element iagree;	
	
	public User(Flow flow) {
		super(flow);
		
		// Common elements
		matError = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("commonelements").get("matError")));
		submitBtn = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("commonelements").get("submitBtn")) );
		accentBtn = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("commonelements").get("accentBtn")));
		// Explore page
		loginBtn =	new Element(this.flow, By.cssSelector(Configuration.getCsss().get("explorepage").get("loginBtn")) );
		profileBtn = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("explorepage").get("profileBtn")) );
		menuMyCampaign = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("explorepage").get("menuMyCampaign")));
		menuLogoutBtn = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("explorepage").get("menuLogoutBtn")) );
		userNameTxt = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("explorepage").get("userNameTxt")) );
		startCampaignTab = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("explorepage").get("startCampaignTab")));
		// Invest page
		slider = new Slider(this.flow, By.cssSelector(Configuration.getCsss().get("investpage").get("slider")));
		campaignStatus = new Slider(this.flow, By.cssSelector(Configuration.getCsss().get("investpage").get("campaignStatus")));
		buyCoritesBtn = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("investpage").get("buyCoritesBtn")));
		iframe = new Element(this.flow, By.cssSelector ("ngx-stripe-card iframe"));
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

	public String getUserFullName() {
		return userFullName;
	}

	public String getUserEmail() {
		return userEmail;
	}
	
//	@Override
	protected void fillLoginForm() {
		emailIn.set(userEmail);
		submitBtn.click();
		checkFormFillingError(true);
		passwordIn.set(userPasswd);
		submitBtn.click();
	}

	public void login (/*LoginMode loginMode*/) {
		
		ACTION.writeln("Login user : " + this.userEmail);		
		flow.setDriver(driver);
		WebDriverWait wait = new WebDriverWait(driver, 15);
		
	    goHome();
		try {
			wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.welcome")) );
			PASSED.writeln("Welcome page was reached");
		} catch (TimeoutException e) {
			FAILED.writeln("Welcome page was not reached");
			flow.makeErrorScreenshot();
			throw new TestFailedException();
		}
		
		try {
			
			if ( loginBtn.exists() ) { // The user is not login
								
				loginBtn.click();
				try {
					wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector("page-account-login")) );
					PASSED.writeln("Login page was reached");
				} catch (TimeoutException e) {
					FAILED.writeln("Login page wasn't reached");
					throw new TestFailedException();
				}
				
				fillLoginForm(); // Virtual method
				
				if ( !userNameTxt.exists() ) {
					FAILED.writeln("Login user was unsaccessful");
					flow.makeErrorScreenshot();
					checkFormFillingError(false);
				} else {
					PASSED.writeln("User '" + userNameTxt.getText() + "' logged in");
				}
				
			} else {
				if ( userNameTxt.exists() ) {
					WARNING.writeln("The user has been already logged in, but expected otherwise : " + this.userEmail 
							+ ". User name: " + userNameTxt.getText());
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
		
		goHome();
		try {
			if ( userNameTxt.exists() ) {
				
				profileBtn.click();
				flow.sleep(50);
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
			PASSED.writeln("Registration page was reached");
		} catch (TimeoutException e1) {
			FAILED.writeln("Registration page wasn't reached");
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

			checkFormFillingError(true);
			// Check email
			try {
				driver.get( getLinkFromEmail(
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

	protected String getLinkFromEmail (String sender, String recipient, String selector) throws InterruptedException {
		String link = "";
		ACTION.writeln("Waiting for email in \"" + Accesses.getEmail().getFolder() + "\" folder >> ");
		for (int i = 0; i < 5; i++) { // Waiting for the email
			Thread.sleep(5000);
		    ImapClient imapClient = new ImapClient (Accesses.getEmail());
		    
			link = imapClient.getHref( sender, recipient, selector );
			
	    	imapClient.close();
			if ( !link.isEmpty() ) break;
		}
		if ( link.isEmpty() ) {
			FAILED.writeln("The link was not received in the email or the time was out." 
					+ "\n\tSender: " + sender
					+ "\n\tRecipient: " + recipient
					+ "\n\tCss selector of the link: " + selector);
			throw new TestFailedException();
		} else {
			PASSED.writeln("The link was received: " + link);
		}
		return link;
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
		DEBUG.writeln("Campaign URL: "+getBaseUrl() + "/explore/" + campaignId);
		driver.get(getBaseUrl() + "/explore/" + campaignId);
		try {
			wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector("page-explore-view")) );
			PASSED.writeln("Page with element 'page-explore-view' was reached");
		} catch (TimeoutException e) {
			FAILED.writeln("Page with element 'page-explore-view' wasn't reached");
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
		
		flow.sleep(100);
		
		flow.makeScreenshot("A");
		
		boolean userHaveCard = accentBtn.exists(); // User have creditcard
		
		DEBUG.writeln("User have credit card: " + userHaveCard);
		if ( userHaveCard ) 
			accentBtn.click();
		else
			submitBtn.click();
		
		if ( iframe.exists() ) {
		
			driver.switchTo().frame(iframe.getEl());
			
			flow.sleep(100);
			
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
	
	private void checkIncorrectCard(String cardNumber) {
		
		TextInput cardNumberIn = new TextInput(this.flow, By.cssSelector ("input[name=cardnumber]"));
		TextInput expDate = new TextInput(this.flow, By.cssSelector ("input[name=exp-date]"));
		TextInput cvc = new TextInput(this.flow, By.cssSelector ("input[name=cvc]"));
		TextInput zip = new TextInput(this.flow, By.cssSelector ("input[name=postal]"));

		slider.set(50); // Buy a half of corites
		
		flow.sleep(100);
		
		boolean userHaveCard = accentBtn.exists(); // User have creditcard
		
		if ( userHaveCard ) // The condition makes the flow to follow by the steps to use a card number even in case the card number is saved 
			accentBtn.click();
		else
			submitBtn.click();
		
		if ( iframe.exists() ) {
			driver.switchTo().frame(iframe.getEl());
			
			flow.sleep(200);
			
			cardNumberIn.set(cardNumber);
			expDate.set("424");
			cvc.set("242");
			zip.set("42424");
			
			flow.sleep(100);
			
			driver.switchTo().defaultContent();
			
			try {
				PASSED.writeln("Incorrect card '" + cardNumber + "' was declined with message: '" + matError.getText() + "'");
			} catch (TimeoutException e) {
				FAILED.writeln("Incorrect card '" + cardNumber + "' was not declined");
				flow.makeErrorScreenshot();
			}
		} else {
			FAILED.writeln("iframe for credit card information is not exist");			
			flow.makeErrorScreenshot();
		}
		accentBtn.click(); // Cancel getting corites
	}
	
	public void buyCoritesIncorrectCard (String campaignId) { // String cardNumber
		
		ACTION.writeln("Buy corites with incorrect cards.");
		flow.setDriver(driver);
		WebDriverWait wait = new WebDriverWait(driver, 5);
		
		// Go to campaign page
		driver.get(getBaseUrl() + "/explore/" + campaignId);
		try {
			wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector("page-explore-view")) );
			PASSED.writeln("Page with element 'page-explore-view' was reached");
		} catch (TimeoutException e) {
			FAILED.writeln("Page with element 'page-explore-view' wasn't reached");
			flow.makeErrorScreenshot();
		}
		
		// Check if the campaign is active
		if ( campaignStatus.getText().equals("ACTIVE") ) {
			PASSED.writeln("Campaign is active");
		} else {
			FAILED.writeln("Campaign is not active. Expected – active");
			flow.makeErrorScreenshot();
		}
		
		buyCoritesBtn.click();
		
		// Go to invest page
		try {
			wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector("page-explore-invest")) );
			PASSED.writeln("Page with element 'page-explore-invest' was reached");
		} catch (TimeoutException e) {
			FAILED.writeln("Page with element 'page-explore-invest' wasn't reached");
			flow.makeErrorScreenshot();
		}
		checkIncorrectCard("4242424242424242"); // Проходит Ok
		checkIncorrectCard("4242424242424241"); // Не проходит Ok
		checkIncorrectCard("4000000000000119"); // Проходит, а не должно
		checkIncorrectCard("4000000000000069"); // Проходит, а не должно
		checkIncorrectCard("4000000000000127"); // Проходит, а не должно
		checkIncorrectCard("4242424242424241"); // Не проходит Ok
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
//		DEBUG.writeln(date.getAttr("min"));
//		date.set(date.getAttr("min"));
		artists.set("Agnetha Fältskog, Björn Ulvaeus, Benny Andersson, Anni-Frid Lyngstad");
		producers.set("Stig Anderson");
		composers.set("Benny Andersson");
		mixers.set("Benny Andersson");
		publishers.set("Sony BMG Music Entertainment");
		countries.click();
		country.click();
		submitBtn.click();
				
//		((JavascriptExecutor)driver).executeScript("scroll(0,400)");
	
	}
	public void makeReadyToRelease(String campaignId) {
		flow.setDriver(driver);
 
		try {
			driver.get(this.getBaseUrl() + "/my-campaigns/" + campaignId + "/release");
			WebDriverWait wait = new WebDriverWait(driver, 5);
			wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("page-campaign-own-release")));
			submitBtn.click();
			PASSED.writeln("Campaign #" + campaignId + " was made 'Ready to release by user '"+ this.getUserFullName() + "'");
		} catch (TestFailedException | TimeoutException e) {
			FAILED.writeln("User '" + this.getUserFullName() +  "' can't make a campaign as 'Ready to release'. Campaign #" + campaignId );
			flow.makeErrorScreenshot();
			throw new TestFailedException();
		}
	}

	public void checkDeclineEmail() {
		flow.setDriver(driver);
		try {
			driver.get( getLinkFromEmail(
				Accesses.getLogins().get("noreply"),	// from
				this.getUserEmail(),					// to
				Configuration.getCsss().get("confirmlinks").get("declined") // confirm link in the letter
			));
//			flow.sleep(1000);
//			flow.checkHtmlHash(); // Эту проверку стоит убрать из-за нестабильности
			WebDriverWait wait = new WebDriverWait(driver, 5);
			wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("page-campaign-own-edit")));
			
		} catch (TestFailedException | InterruptedException | TimeoutException e) {
			FAILED.writeln("Problem with receiving an email with campaign declining message. User email: " + this.getUserEmail() );
			throw new TestFailedException();
		}
		
	}

	public void checkEmailLink(String linkCssSelector, String targetPageElementCssSelector) {
		
		ACTION.writeln("Check email from: '" + Accesses.getLogins().get("noreply") + "', to: '" + this.getUserEmail() + "', about: '" + linkCssSelector + "'");
		flow.setDriver(driver);
		try {
			driver.get( getLinkFromEmail(
				Accesses.getLogins().get("noreply"),	// from
				this.getUserEmail(),					// to
				linkCssSelector  // CssSelector of link in the letter
			));
			WebDriverWait wait = new WebDriverWait(driver, 5);
			wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(targetPageElementCssSelector)));
			PASSED.writeln("Target page by the email link was reached. Title: " + driver.getTitle() + ". Current URL: " + driver.getCurrentUrl() + ". Css selector: " + targetPageElementCssSelector );
			
		} catch (TestFailedException | InterruptedException | TimeoutException e) {
			FAILED.writeln("Problem with receiving an email with campaign declining message. User email: " + this.getUserEmail() );
			throw new TestFailedException();
		}		
	}

}

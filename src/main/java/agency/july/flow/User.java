package agency.july.flow;

import static agency.july.logger.Logevent.*;

import java.io.File;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import agency.july.config.models.Accesses;
import agency.july.config.models.BrowserProps;
import agency.july.config.models.Configuration;
import agency.july.factory.BrowserFactory;
import agency.july.sendmail.ImapClient;
import agency.july.webelements.Element;
import agency.july.webelements.Slider;
import agency.july.webelements.TextInput;

public class User /*extends Test*/ {
	
    protected WebDriver driver;
    protected Flow flow;
    protected int defaultImplicitly = 10;
	
	protected String userEmail = "No present";
	protected String userFullName = "Noname";
	protected String userFirstName = "Noname";
	protected String userLastName = "Noname";
	protected String userPasswd = "";
	protected String userTel = "No present";
	
	// Stripe iframe
	private TextInput cardNumber;
	private TextInput expDate;
	private TextInput cvc;
	private TextInput zip;

	// Common elements
	private Element matError;
	private Element submitBtn;
	private Element accentBtn;
	
	// Explore page
	private Element loginBtn;
	protected Element profileBtn;
	private Element menuMyCampaigns;
	private Element menuPortfolio;
	private Element menuProfile;
	private Element menuPayouts;
	private Element menuLogoutBtn;
	private Element userNameTxt;
	private Element startCampaignTab;
	private Element exploreTab;
	
	// Campaign view page
	By campaignViewPageBy = By.cssSelector("page-explore-view");
	private Element buyCoritesBtn;
	
	// Portfolio page
	By portfolioPageBy = By.cssSelector("page-portfolio");
	private Element releasedCampaignName;
	private Element releasedMyCorites;

	// Payouts page
	By payoutsPageBy = By.cssSelector("user-payout-balance");

	// Invest page
	private Slider slider;
	private Element anotherCardBtn;
	private Element newCardBtn;
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
	
	// Start Campaign page
	private TextInput campaignImage;
	private TextInput campaignSong;
	private TextInput campaignName;
	private TextInput campaignArtist;
	private TextInput campaignGenre;
	private Element campaignGenreSelect;
	private TextInput textArea;
	private Element iagree;
	
	public User(Flow flow, BrowserProps props) {
		this.driver = BrowserFactory.getDriver(props);
		this.flow = flow;
		
		// Stripe iframe
		cardNumber = new TextInput(this.flow, By.cssSelector ("input[name=cardnumber]"));
		expDate = new TextInput(this.flow, By.cssSelector ("input[name=exp-date]"));
		expDate.setFirstWait(280);
		cvc = new TextInput(this.flow, By.cssSelector ("input[name=cvc]"));
		zip = new TextInput(this.flow, By.cssSelector ("input[name=postal]"));
		
		// Common elements
		matError = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("commonelements").get("matError")));
		submitBtn = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("commonelements").get("submitBtn")) );
		accentBtn = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("commonelements").get("accentBtn")));
		// Explore page
		loginBtn =	new Element(this.flow, By.cssSelector(Configuration.getCsss().get("explorepage").get("loginBtn")) );
		profileBtn = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("explorepage").get("profileBtn")) );
		menuMyCampaigns = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("explorepage").get("menuMyCampaigns")));
		menuPortfolio = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("explorepage").get("menuPortfolio")));
		menuProfile = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("explorepage").get("menuProfile")));
		menuPayouts = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("explorepage").get("menuPayouts")));
		menuLogoutBtn = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("explorepage").get("menuLogoutBtn")) );
		userNameTxt = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("explorepage").get("userNameTxt")) );
		startCampaignTab = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("explorepage").get("startCampaignTab")));
		exploreTab = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("explorepage").get("exploreTab")));
		// Portfolio page
		releasedCampaignName = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("portfoliopage").get("releasedCampaignName")));
		releasedMyCorites = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("portfoliopage").get("releasedMyCorites")));
		// Campaign view page
		buyCoritesBtn = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("investpage").get("buyCoritesBtn")));
		// Invest page
		slider = new Slider(this.flow, By.cssSelector(Configuration.getCsss().get("investpage").get("slider")));
		anotherCardBtn = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("investpage").get("anotherCardBtn")));
		newCardBtn = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("investpage").get("newCardBtn")));
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

	public String getFirstName() {
		return userFirstName;
	}

	public String getLastName() {
		return userLastName;
	}

	public String getUserFullName() {
		return userFullName;
	}

	public String getUserEmail() {
		return userEmail;
	}
	
	protected void fillLoginForm() {
		emailIn.set(userEmail);
		submitBtn.click();
		checkFormFillingError(true);
		passwordIn.set(userPasswd);
		submitBtn.click();
	}

	public void login () {
		
		ACTION.writeln("Login user : " + this.userEmail);		
		flow.setDriver(driver);
		WebDriverWait wait = new WebDriverWait(driver, 15);
		
	    goHome();
		try {
			wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.welcome")) );
			PASSED.writeln("Welcome page was reached");
		} catch (TimeoutException e) {
			FAILED.writeln("Welcome page was not reached or user '" + this.getUserFullName() + "' has been already logged in");
			flow.makeErrorScreenshot();
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
				
			} else if ( userNameTxt.exists() ) 
					WARNING.writeln("The user has been already logged in, but expected otherwise : " + this.userEmail 
							+ ". User name: " + userNameTxt.getText());
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
	
    public void navigateToExplore() {
//		ACTION.writeln("Navigate to Explore list" + this.getUserFullName());
    	driver.findElement(By.cssSelector(Configuration.getCsss().get("explorepage").get("exploreTab"))).click();
	}

	public void navigateToPortfolio() {
		ACTION.writeln("Navigate to portfolio of user: " + this.getUserFullName());
		flow.setDriver(driver);
		
		profileBtn.click();
		menuPortfolio.click();
		
		try {
			WebDriverWait wait = new WebDriverWait(driver, 5);
			wait.until(ExpectedConditions.presenceOfElementLocated( portfolioPageBy ));
			PASSED.writeln("Portfolio page was reached");
		} catch (TimeoutException e) {
			FAILED.writeln("Portfolio page wasn't reached");
			throw new TestFailedException();			
		}	
	}

	public void navigateToPayouts() {
		ACTION.writeln("Navigate to payouts balance of user: " + this.getUserFullName());
		flow.setDriver(driver);
		
		profileBtn.click();
		menuPayouts.click();
		
		try {
			WebDriverWait wait = new WebDriverWait(driver, 5);
			wait.until(ExpectedConditions.presenceOfElementLocated( payoutsPageBy ));
			PASSED.writeln("Payouts page was reached");
		} catch (TimeoutException e) {
			FAILED.writeln("Payouts page wasn't reached");
			throw new TestFailedException();			
		}	
	}

	public void navigateToCampaign(String campaignId) {
		ACTION.writeln("Navigate to campaign #" + campaignId);
		flow.setDriver(driver);
		
		String cssS = Configuration.getCsss().get("explorepage").get("teaserCampaign").replace("{id}", campaignId);
		Element campaign = new Element(this.flow, By.cssSelector (cssS));
		
		exploreTab.click();
		campaign.click();
		
		try {
			WebDriverWait wait = new WebDriverWait(driver, 5);
			wait.until(ExpectedConditions.presenceOfElementLocated( campaignViewPageBy ));
			PASSED.writeln("Campaign view page was reached");
		} catch (TimeoutException e) {
			FAILED.writeln("Campaign view page wasn't reached");
			throw new TestFailedException();			
		}	
	}
	
	public void register () {
		
		ACTION.writeln("Register an user: " + this.userEmail);
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
			submitBtn.click();

			checkFormFillingError(true);

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
		for (int i = 0; i < 20; i++) { // Waiting for the email
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
	
	protected String getLinkFromEmail (String sender, String recipient, String selector, String content) throws InterruptedException {
		String link = "";
		ACTION.writeln("Waiting for email in \"" + Accesses.getEmail().getFolder() + "\" folder >> ");
		for (int i = 0; i < 20; i++) { // Waiting for the email
			Thread.sleep(5000);
		    ImapClient imapClient = new ImapClient (Accesses.getEmail());
		    
			link = imapClient.getHref( sender, recipient, selector, content );
			
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
	
	public void startCampaign (String name) {
		
		campaignImage = new TextInput(this.flow, By.cssSelector(Configuration.getCsss().get("startcampaignpage").get("campaignImage"))/*, By.cssSelector("page-my-campaigns")*/);
		campaignSong = new TextInput(this.flow, By.cssSelector(Configuration.getCsss().get("startcampaignpage").get("campaignSong")));
		campaignName = new TextInput(this.flow, By.cssSelector(Configuration.getCsss().get("startcampaignpage").get("campaignName")));
		campaignArtist = new TextInput(this.flow, By.cssSelector(Configuration.getCsss().get("startcampaignpage").get("campaignArtist")));
		campaignGenre = new TextInput(this.flow, By.cssSelector(Configuration.getCsss().get("startcampaignpage").get("campaignGenre")));
		campaignGenreSelect = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("startcampaignpage").get("campaignGenreSelect")));
		textArea = new TextInput(this.flow, By.cssSelector(Configuration.getCsss().get("startcampaignpage").get("textArea")));
		iagree = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("startcampaignpage").get("iagree")));
		
		ACTION.writeln("Start Campaign: '" + name + "'");
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
				wait.until( ExpectedConditions.textToBe(By.cssSelector("app-audio > div > app-slider > div.max"), "00:27") );
				PASSED.writeln("Audio has been uploaded");
			} catch (TimeoutException e1) {
				flow.makeErrorScreenshot();
				FAILED.writeln("Problem with uploading audio. Timeout 50 sec. " + driver.findElement(By.cssSelector("app-audio > div > app-slider > div.max")).getText() );
			}
			
			campaignName.set(name);
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
				PASSED.writeln("Next page with element 'page-campaign-own-edit' was reached");
			} catch (TimeoutException e) {
				FAILED.writeln("Next page with element 'page-campaign-own-edit' was not reached");
				throw new TestFailedException();
			}
			
			profileBtn.click();
			menuMyCampaigns.click();

			try {
				wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector("page-campaign-own-list")) ); //
				PASSED.writeln("Next page with element 'page-campaign-own-list' was reached");
			} catch (TimeoutException e) {
				FAILED.writeln("Next page with element 'page-campaign-own-list' was not reached");
				throw new TestFailedException();
			}
			
		} catch (TestFailedException e) {
			throw new TestFailedException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new TestFailedException();
		}
	}
	
	public void buyCorites (String campaignId, int quantity) {
				
		ACTION.writeln("Buy corites. User: '" + this.getUserFullName() + "'");
		flow.setDriver(driver);
		WebDriverWait wait = new WebDriverWait(driver, 10);
		
		// Go to campaign page
		navigateToCampaign(campaignId);

		// Check if the campaign is active — there is "Get corites" button
		if ( buyCoritesBtn.exists() ) {
			PASSED.writeln("Campaign is active");
		} else {
			FAILED.writeln("Campaign is not active. Expected – active");
			throw new TestFailedException();
		}
		
		buyCoritesBtn.click();
		
		slider.set(quantity); // Buy corites (percentage of rest balance)
		flow.waitForStableLocation(By.cssSelector("div.mat-slider-thumb"), 160);

		submitBtn.click();
		
		// Go to invest page
		try {
			wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector("page-explore-invest")) );
			PASSED.writeln("Page with element 'page-explore-invest' was reached");
		} catch (TimeoutException e) {
			FAILED.writeln("Page with element 'page-explore-invest' was not reached");
			throw new TestFailedException();
		}

		boolean userHaveCard = anotherCardBtn.exists(); // User have creditcard
		
		if ( userHaveCard ) 
			anotherCardBtn.click();
		else
			newCardBtn.click();
		
		if ( iframe.exists() ) {
			
			int i = 0;		
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
			
			cardNumber.set("4242424242424242");
			expDate.set("424");
			cvc.set("242");
			zip.set("42424");
			
			driver.switchTo().defaultContent();
			
			if ( userHaveCard ) {
				submitBtn.click(); // Get corites
				
				if ( thankyou.exists() ) {
				
					PASSED.writeln("Thankyou invest page was reached.");
/*					
					if ( quantity == 100 ) { // If it was bought 100% of corites then therefore the campaign is FUNDED
						navigateToCampaign(campaignId);
					}
*/					
				} else {
					FAILED.writeln("Thankyou invest page was not reached.");
					flow.makeErrorScreenshot();
				}
			} else {
				accentBtn.click(); // Cancel getting corites
				exploreTab.click(); // Navigate from invest page to unblock the order immediately
			}
		} else {
			FAILED.writeln("iframe for credit card information is not exist. See screenshot: 'Error_buyCorites_2'");			
			flow.makeErrorScreenshot();
		}
	}
	
	public void checkIncorrectCard(String campaignId, String card) {
		
		ACTION.writeln("Buy corites with incorrect cards.");
		flow.setDriver(driver);
		WebDriverWait wait = new WebDriverWait(driver, 5);
		
		// Go to campaign page
		navigateToCampaign(campaignId);
		try {
			wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector("page-explore-view")) );
			PASSED.writeln("Page with element 'page-explore-view' was reached");
		} catch (TimeoutException e) {
			FAILED.writeln("Page with element 'page-explore-view' wasn't reached");
			flow.makeErrorScreenshot();
		}
		
		// Check if the campaign is active — there is "Get corites" button
		if ( buyCoritesBtn.exists() ) {
			PASSED.writeln("Campaign is active");
		} else {
			FAILED.writeln("Campaign is not active. Expected – active");
			throw new TestFailedException();
		}
		
		buyCoritesBtn.click();

		slider.set(50); // Buy a half of corites
		flow.waitForStableLocation(By.cssSelector("div.mat-slider-thumb"), 160);
		
		submitBtn.click();
		// Go to invest page
		try {
			wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector("page-explore-invest")) );
			PASSED.writeln("Page with element 'page-explore-invest' was reached");
		} catch (TimeoutException e) {
			FAILED.writeln("Page with element 'page-explore-invest' was not reached");
			throw new TestFailedException();
		}

		boolean userHaveCard = anotherCardBtn.exists(); // User have creditcard	
	
		if ( userHaveCard ) 
			anotherCardBtn.click();
		else
			newCardBtn.click();
		
		if ( iframe.exists() ) {
			
			int i = 0;		
			do {
				driver.switchTo().defaultContent();
				flow.sleep(100);
				driver.switchTo().frame(driver.findElement(iframe.getBy()));
			} while ( driver.findElements(By.cssSelector("div#root > form")).size() == 0 && i++ < 20 );
			
			try {
				wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector("div#root > form")) );
				PASSED.writeln("There is access to 'Stripe' iframe on the invest page");
			} catch (TimeoutException e) {
				FAILED.writeln("There isn't access to 'Stripe' iframe on the invest page");
				flow.makeErrorScreenshot();
				flow.makeErrorPageSource();
				driver.switchTo().frame(0);
				flow.sleep(1000);
			}
			
			cardNumber.set(card);
			expDate.set("424");
			cvc.set("242");
			zip.set("42424");
			
			flow.sleep(100);
			
			driver.switchTo().defaultContent();
			
			try {
				PASSED.writeln("Incorrect card '" + card + "' was declined with message: '" + matError.getText() + "'");
			} catch (TimeoutException e) {
				FAILED.writeln("Incorrect card '" + card + "' was not declined");
				flow.makeErrorScreenshot();
			}
		} else {
			FAILED.writeln("iframe for credit card information is not exist");			
			flow.makeErrorScreenshot();
		}
		accentBtn.click(); // Cancel getting corites
		exploreTab.click(); // Navigate from invest page to unblock the order immediately
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
				
		((JavascriptExecutor)driver).executeScript("scroll(0,-10)");
	
	}
	public void makeReadyToRelease(String campaignId) {
		
		ACTION.writeln("Make campaing #" + campaignId + " 'Ready to release' by user: '" + this.getUserFullName() + "'" );
		flow.setDriver(driver);
 
		try {
			driver.get(this.getBaseUrl() + "/my-campaigns/" + campaignId + "/release");
			WebDriverWait wait = new WebDriverWait(driver, 5);
			wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("page-campaign-own-release")));
			submitBtn.click();
			PASSED.writeln("Campaign #" + campaignId + " was made 'Ready to release' by user '"+ this.getUserFullName() + "'");
		} catch (TestFailedException | TimeoutException e) {
			FAILED.writeln("User '" + this.getUserFullName() +  "' can't make a campaign as 'Ready to release'. Campaign #" + campaignId );
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
			FAILED.writeln("Problem with receiving an email or navigating to the target page by the email link. User email: " + this.getUserEmail() );
			throw new TestFailedException();
		}		
	}

	public void checkPortfolioList(String campaignName) {
		ACTION.writeln("Check portfolio list for Campaign name: '" + campaignName + "'");
		flow.setDriver(driver);
		WebDriverWait wait = new WebDriverWait(driver, 5);
		
		try {
			wait.until(ExpectedConditions.presenceOfElementLocated( portfolioPageBy ));
		} catch (TimeoutException e) {
			FAILED.writeln("Portfolio page is required. " + portfolioPageBy + " Use navigateToPortfolio() before.");
			throw new TestFailedException();			
		}
		
		try {
			wait.until(ExpectedConditions.presenceOfElementLocated( releasedCampaignName.getBy() ));
			if (campaignName.equals( releasedCampaignName.getText() ) ) 
				PASSED.writeln("Portfolio list of user '" + this.userFullName + "' contains a campaign with name '" + campaignName + "'" );
			else {
				FAILED.writeln("Portfolio list of user '" + this.userFullName + "' is wrong");
				flow.makeErrorScreenshot();
			}
		} catch (TestFailedException | TimeoutException e) {
			FAILED.writeln("Portfolio list of user '" + this.userFullName + "' is wrong");
			throw new TestFailedException();			
		}		

	}

	public void checkMyCorites(int myCorites) {
		ACTION.writeln("Checking amount of corite of a released campaign");
		flow.setDriver(driver);
		WebDriverWait wait = new WebDriverWait(driver, 5);
		
		try {
			wait.until(ExpectedConditions.presenceOfElementLocated( portfolioPageBy ));
		} catch (TimeoutException e) {
			FAILED.writeln("Portfolio page is required. " + portfolioPageBy + " Use navigateToPortfolio() before.");
			throw new TestFailedException();			
		}
		
		try {
			wait.until(ExpectedConditions.presenceOfElementLocated( releasedMyCorites.getBy() ));
			int actualMyCorites = Integer.parseInt( releasedMyCorites.getText() );
			if ( actualMyCorites == myCorites ) 
				PASSED.writeln("Amount of corite is expected: " + actualMyCorites + ". User: '" + this.userFullName + "'");
			else {
				FAILED.writeln("Amount of corite of a released campaign is unexpected: " + actualMyCorites + " Expected: " + myCorites + ". User: '" + this.userFullName + "'");
				flow.makeErrorScreenshot();
			}
		} catch (TestFailedException | TimeoutException e) {
			FAILED.writeln("Portfolio list of user '" + this.userFullName + "' is wrong");
			throw new TestFailedException();			
		}
	}
	
	public void checkPayoutsBalance(float balance) {
		ACTION.writeln("Checking payouts balance");
		flow.setDriver(driver);
		
		navigateToPayouts();
		
		float actualBalance = Float.parseFloat( driver.findElement(payoutsPageBy).getText().replaceAll("[^\\d\\.]", "") );
		if (balance == actualBalance) 
			PASSED.writeln("Payouts balance is expected: " + actualBalance + ". User: '" + this.userFullName + "'");
		else {
			FAILED.writeln("Payouts balance is unexpected: " + actualBalance + " Expected: " + balance + ". User: '" + this.userFullName + "'");			
			flow.makeErrorScreenshot();
		}
	}
	
    public void checkCampaignInList(String campaignId) {
		ACTION.writeln("Check if there is campaign #" + campaignId + " in Explore list");
    	
    	flow.setDriver(driver);
    	navigateToExplore();
    	
		String cssS = Configuration.getCsss().get("explorepage").get("teaser").replace("{id}", campaignId);

    	try {
    		WebDriverWait wait = new WebDriverWait(driver, 10);
			wait.until( ExpectedConditions.presenceOfElementLocated(By.cssSelector(cssS)) );
    		PASSED.writeln("There is campaign #" + campaignId + " on Explore list");
	    } catch (TimeoutException e) {
			FAILED.writeln("There isn't campaign #" + campaignId + " on Explore list");
			flow.makeErrorScreenshot();    	
	    }
    }
    
	public String getBaseUrl() {
		return Accesses.getUrls().get("base");
	}
	
    public void goHome() { 
    	driver.get(Accesses.getUrls().get("base"));
    }
    
	public void checkFormFillingError(boolean throwExeption) {
		
    	this.driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);

		Element matError = new Element(this.flow, By.cssSelector("mat-error"));
		if ( matError.exists() ) {
			String errorText = matError.getText();
			FAILED.writeln("Form filling error: '" + errorText + "'" );
			if ( throwExeption ) throw new TestFailedException();
		}
		
		matError = new Element(this.flow, By.cssSelector(".mat-error"));
		if ( matError.exists() ) {
			FAILED.writeln("Form filling error: '" + matError.getText() + "'" );
			if ( throwExeption ) throw new TestFailedException();
		}
		
    	this.driver.manage().timeouts().implicitlyWait(this.defaultImplicitly, TimeUnit.SECONDS);
	}

	public void teardown () {
    	driver.quit();
    }

}

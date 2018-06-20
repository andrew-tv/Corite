package agency.july.flow;

import static agency.july.logger.Logevent.*;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import agency.july.config.models.Accesses;
import agency.july.config.models.Configuration;
import agency.july.webelements.Element;
import agency.july.webelements.TextInput;

public class Admin extends User {
	
	private final String baseurl = Accesses.getUrls().get("adminbase");
	private final String allUsers = baseurl + "/?entity=User&action=list&menuIndex=10&submenuIndex=-1";
	private final String allCampaigns = baseurl + "/?entity=Campaign&action=list&menuIndex=3&submenuIndex=-1";
	private final String editUser43 = baseurl + "/?entity=User&action=edit&id=43";

	// Explore page
	private Element menuAdmin;
	
	// Admin page
	private TextInput searchQuery;	
	private Element searchBtn;	
	private Element dropdownMenuBtn;	
	private Element deleteUserBtn;	
	private Element confirmDeleteUserBtn;
	private Element detectedItem;
	
	// Moderation page	    
	private Element acceptedModeration;	
	private Element saveChangesModeration;	

	public Admin(Flow flow) {
		super(flow);
//	    goHome();
	    
		// Explore page	    
	    menuAdmin = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("explorepage").get("menuAdmin")));
		// Admin page	    
		searchQuery = new TextInput(this.flow, By.cssSelector(Configuration.getCsss().get("adminpage").get("searchQuery")));	
		searchBtn = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("adminpage").get("searchBtn")));	
		dropdownMenuBtn = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("adminpage").get("dropdownMenuBtn")));	
		deleteUserBtn = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("adminpage").get("deleteUserBtn")));	
		confirmDeleteUserBtn = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("adminpage").get("confirmDeleteUserBtn")));
		detectedItem = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("adminpage").get("detectedItem")));
		// Moderation page	    
		acceptedModeration = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("moderationpage").get("acceptedModeration")));
		saveChangesModeration = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("moderationpage").get("saveChangesModeration")));

	}
	
	public Admin withUser(String user) {
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
	public String getBaseUrl() {
    	return baseurl;
    }
    
    public String getCampaignId(String campaignTitle) {
    	
		ACTION.writeln("Get campaign ID by title : " + campaignTitle);		
		flow.setDriver(driver);
		Element searshResultsNumber = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("adminpage").get("searshResultsNumber"))); 
		String campaignId = "";
		
		try {
			
			WebDriverWait wait = new WebDriverWait(driver, 5);
			driver.get(allCampaigns);
			wait.until(ExpectedConditions.titleContains("All campaigns"));

			searchQuery.set(campaignTitle);
			searchBtn.click();
			
			int resNum = Integer.parseInt( searshResultsNumber.getText() );
			if (resNum > 0) {
				campaignId = detectedItem.getAttr("data-id");
				PASSED.writeln("Campaign ID has been got. Id = " + campaignId);
			} else {
				FAILED.writeln("Campaign ID has not been got");
			}
			
		} catch (TestFailedException e) {
			throw new TestFailedException();			
		} catch (Exception e) {
			FAILED.writeln("Campaign ID has not been got");
			e.printStackTrace();
			throw new TestFailedException();
		}
    	return campaignId; 
    }

	public void gotoAdminPage() {
		
		ACTION.writeln("Goto admin page : " + this.userEmail);		
		flow.setDriver(driver);

		WebDriverWait wait = new WebDriverWait(driver, 5);
		
		try {
			profileBtn.click();
			Thread.sleep(100); // wait for ending animation
			menuAdmin.click();
			// Change window
			for( String winHandle : driver.getWindowHandles() ) {
			    driver.switchTo().window(winHandle);
			}
			
	        driver.manage().window().setSize(new Dimension(1300, 800));
//	        driver.get( this.getBaseUrl() );
			wait.until(ExpectedConditions.titleContains("Dashboard"));
			
		} catch (TestFailedException e) {
			throw new TestFailedException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new TestFailedException();
		}
	}
	
	public void setModeratorRole43(boolean tick) {
		WebDriverWait wait = new WebDriverWait(driver, 5);
		driver.get(editUser43);
		wait.until(ExpectedConditions.titleContains("Edit User (#43)"));
		
		WebElement checkboxModerator = driver.findElement(By.cssSelector("input[value='ROLE_MODERATOR']"));
		if ( tick && checkboxModerator.getAttribute("checked") == null ) checkboxModerator.click();
		if ( !tick && checkboxModerator.getAttribute("checked") != null ) checkboxModerator.click();
		driver.findElement(By.cssSelector("button.action-save")).click();		
	}
	
	public void removeUser(String fullName) {
		
		ACTION.writeln("Remove a user: " + fullName);		
		flow.setDriver(driver);
		driver.get(allUsers);
		
		try {
			
			WebDriverWait wait = new WebDriverWait(driver, 5);
			driver.get(allUsers);
			wait.until(ExpectedConditions.titleContains("All users"));

			searchQuery.set(fullName);
			searchBtn.click();
			if ( dropdownMenuBtn.exists() ) { // Remove the user if he exists
				dropdownMenuBtn.click();
				deleteUserBtn.click();
				confirmDeleteUserBtn.click();
				try {
					wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("td.no-results")));
					PASSED.writeln("User has been removed");
				} catch (TimeoutException e) {
					FAILED.writeln("User has not been removed or there ara other user(s) with the same full name");
					flow.makeErrorScreenshot();
				}
			}
			
		} catch (Exception e) {
			FAILED.writeln("Remove user error. User: " + fullName);
			e.printStackTrace();
			throw new TestFailedException();
		}

	}

	public void removeCampaignByName(String campaignName) {
		
		ACTION.writeln("Remove campaign '" + campaignName + "'");		
		flow.setDriver(driver);
		WebDriverWait wait = new WebDriverWait(driver, 5);
		driver.get(allCampaigns);
		wait.until(ExpectedConditions.titleContains("All campaigns"));
		
		try {
			
			searchQuery.set(campaignName);
			searchBtn.click();
			dropdownMenuBtn.click();
			deleteUserBtn.click();
			confirmDeleteUserBtn.click();
			
			try {
				wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("td.no-results")));
				PASSED.writeln("Campaign '" + campaignName + "' was removed");
			} catch (TimeoutException e) {
				FAILED.writeln("Campaign was not removed or there are other campaigns with the same name");				
			}
			
		} catch (TestFailedException e) {
			throw new TestFailedException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new TestFailedException();
		}

	}
	
	public void removeCampaignOrders(String campaignId) {
		
		ACTION.writeln( "Remove all orders of campaign #" + campaignId );		
		flow.setDriver(driver);
		driver.get(this.getBaseUrl() + "?entity=Order&action=list");

		List<WebElement> rows = driver.findElements(By.cssSelector("tr"));
		
		for(int i = 1; i<rows.size(); i++) {
			WebElement foundCampaign = rows.get(i).findElement(By.cssSelector("td[data-label=Campaign] > a"));
			if ( foundCampaign.getText().equals("Campaign #" + campaignId) ) {
				WebElement deleteBtn = rows.get(i).findElement(By.cssSelector(".text-danger.action-delete"));
				deleteBtn.click();
				confirmDeleteUserBtn.click();
				rows = driver.findElements(By.cssSelector("tr"));
			}			
		}
	}

	public void acceptCampaignByEmail() {
		flow.setDriver(driver);
		ACTION.writeln("Accept a campaign by email");
		// Check email
		try {
			driver.get( getLinkFromEmail(
				Accesses.getLogins().get("noreply"),	// from
				this.userEmail,							// to
				Configuration.getCsss().get("confirmlinks").get("moderation") // confirm link in the letter
			));
			
			acceptedModeration.click();
			saveChangesModeration.click();
			
		} catch (WebDriverException e) {
			FAILED.writeln("The URL for moderation a new campaign is wrong");
			flow.makeErrorScreenshot();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
	}
	
	public CompaignStatus getCampaignStatus(String campaignId) {
		
		ACTION.writeln("Get status for Campaign #" + campaignId);
		flow.setDriver(driver);
		driver.get( this.getBaseUrl() + "/?entity=Campaign&action=edit&id=" + campaignId );
		WebDriverWait wait = new WebDriverWait(driver, 5);
		wait.until(ExpectedConditions.titleContains("Moderate Campaign (#" + campaignId + ")"));
		
		List <WebElement> checkboxes = driver.findElements(By.cssSelector("input[name='campaign[moderationStatus]']"));
		for (WebElement el : checkboxes) {
			if (el.getAttribute("checked") != null ) {
				switch ( el.getAttribute("value") ) {
					case "0" : return CompaignStatus.NONE;
					case "2" : return CompaignStatus.NEEDS_MODERATION;
					case "1" : return CompaignStatus.ACCEPT;
					case "-1" : return CompaignStatus.DECLINE;
				}
			}
		}
		FAILED.writeln("Status for Campaign #" + campaignId + "don't receive");
		return null;
	}

	public void moderateCampaign(String campaignId, CompaignStatus status) {
		
		ACTION.writeln("Moderate Campaign #" + campaignId + " to " + status + " status");
		flow.setDriver(driver);
		driver.get( this.getBaseUrl() + "/?entity=Campaign&action=edit&id=" + campaignId );
		WebDriverWait wait = new WebDriverWait(driver, 5);
		wait.until(ExpectedConditions.titleContains("Moderate Campaign (#" + campaignId + ")"));

		WebElement checkboxModerator = null;
		
		switch (status) {
		case NONE :
			checkboxModerator = driver.findElement(By.cssSelector("input[name='campaign[moderationStatus]'][value='0']"));
			break;
		case NEEDS_MODERATION :
			checkboxModerator = driver.findElement(By.cssSelector("input[name='campaign[moderationStatus]'][value='2']"));
			break;
		case ACCEPT :
			checkboxModerator = driver.findElement(By.cssSelector("input[name='campaign[moderationStatus]'][value='1']"));
			break;
		case DECLINE :
			checkboxModerator = driver.findElement(By.cssSelector("input[name='campaign[moderationStatus]'][value='-1']"));
			break;
		}
		if ( checkboxModerator.getAttribute("checked") == null ) {
			checkboxModerator.click();
			flow.sleep(500); // Без этой задержки работает нестабильно при DECLINE. Видимо из-за прорисовки окна для ввода сообщения			
			driver.findElement(By.cssSelector("button.action-save")).click();		
		}

	}

	public void setReleaseStatus(String campaignId, ReleaseStatus status) {
		ACTION.writeln("Set Release Status of Campaign #" + campaignId + " to " + status + " status");
		flow.setDriver(driver);
		driver.get( this.getBaseUrl() + "/?entity=Release&action=changeReleaseStatus&id=" + campaignId );
		WebDriverWait wait = new WebDriverWait(driver, 5);
		wait.until(ExpectedConditions.titleContains("Set release status (#" + campaignId + ")"));
		
		WebElement checkboxModerator = null;
		
		switch (status) {
		case READY_TO_RELEASE :
			checkboxModerator = driver.findElement(By.cssSelector("input[name='release[status]'][value='2']"));
			break;
		case RELEASED :
			checkboxModerator = driver.findElement(By.cssSelector("input[name='release[status]'][value='3']"));
			break;
		}
		if ( checkboxModerator.getAttribute("checked") == null ) {
			checkboxModerator.click();
			driver.findElement(By.cssSelector("button.action-save")).click();		
		}

	}

}

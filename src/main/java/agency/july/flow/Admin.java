package agency.july.flow;

import static agency.july.logger.Logevent.*;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
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
	private final String allUsers = baseurl + "/?entity=User&action=list&menuIndex=9&submenuIndex=-1";
	private final String allCampaigns = baseurl + "/?entity=Campaign&action=list&menuIndex=1&submenuIndex=-1";
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
			
			String resNum = searshResultsNumber.getText();
			if (resNum.equals("1")) {
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
			menuAdmin.click();
			// Change window
			for(String winHandle : driver.getWindowHandles()){
			    driver.switchTo().window(winHandle);
			}
			
	        driver.manage().window().setSize(new Dimension(1300, 800));
			wait.until(ExpectedConditions.titleContains("All campaigns"));
			
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
	
	public void removeUser(String user) {
		
		ACTION.writeln("Remove an user : " + user);		
		flow.setDriver(driver);
		
		try {
			
			WebDriverWait wait = new WebDriverWait(driver, 5);
			driver.get(allUsers);
			wait.until(ExpectedConditions.titleContains("All users"));

			searchQuery.set(user);
			searchBtn.click();
			dropdownMenuBtn.click();
			deleteUserBtn.click();
			confirmDeleteUserBtn.click();
			
			wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("td.no-results")));
			
			PASSED.writeln("User has been removed");
			
		} catch (Exception e) {
			FAILED.writeln("User has not been removed");
			e.printStackTrace();
			throw new TestFailedException();
		}

	}

	public void removeCampaign(String campaignName) {
		
		ACTION.writeln("Remove campaign");		
		flow.setDriver(driver);
		
		try {
			
			searchQuery.set(campaignName);
			searchBtn.click();
			dropdownMenuBtn.click();
			deleteUserBtn.click();
			confirmDeleteUserBtn.click();
			
			flow.waitForHtmlHash(By.cssSelector("td.no-results"));
			
		} catch (TestFailedException e) {
			throw new TestFailedException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new TestFailedException();
		}

	}

	public void confirmModeration() {
		flow.setDriver(driver);

		// Check email
		try {
			driver.get( getToken(
				Accesses.getLogins().get("noreply"),	// from
				this.userEmail,							// to
				Configuration.getCsss().get("confirmlinks").get("moderation") // confirm link in the letter
			));
			
//			WebDriverWait wait = new WebDriverWait(driver, 5);
//			wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.moderation-form")));

			acceptedModeration.click();
			saveChangesModeration.click();
			
		} catch (WebDriverException e) {
			FAILED.writeln("The URL for moderation a new campaign is wrong");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}

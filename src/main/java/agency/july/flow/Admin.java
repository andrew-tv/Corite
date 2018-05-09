package agency.july.flow;

import static agency.july.logger.Logevent.ACTION;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import agency.july.config.models.Accesses;
import agency.july.config.models.Configuration;
import agency.july.webelements.Element;
import agency.july.webelements.TextInput;

public class Admin extends User {
	
	private final String allUsers = Accesses.getUrls().get("admindev") + "/?entity=User&action=list&menuIndex=9&submenuIndex=-1";

	// Explore page
	private Element menuAdmin;
	
	// Admin page
	private TextInput searchQuery;	
	private Element searchBtn;	
	private Element dropdownMenuBtn;	
	private Element deleteUserBtn;	
	private Element confirmDeleteUserBtn;
	

	public Admin(Flow flow) {
		super(flow);
	    goHome();
	    
		// Explore page	    
	    menuAdmin = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("explorepage").get("menuAdmin")));
		// Admin page	    
		searchQuery = new TextInput(this.flow, By.cssSelector(Configuration.getCsss().get("adminpage").get("searchQuery")));	
		searchBtn = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("adminpage").get("searchBtn")));	
		dropdownMenuBtn = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("adminpage").get("dropdownMenuBtn")));	
		deleteUserBtn = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("adminpage").get("deleteUserBtn")));	
		confirmDeleteUserBtn = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("adminpage").get("confirmDeleteUserBtn")));

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

	public void gotoAdminPage() {
		
		ACTION.writeln("Goto admin page : " + this.userEmail);		
		flow.setDriver(driver);

		WebDriverWait wait = new WebDriverWait(driver, 5);
		
		try {
			profileBtn.click();
			menuAdmin.click(); //        menuAdmin: "a.mat-menu-item.ng-star-inserted"
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
	
	public void removeUser(String user) {
		
		ACTION.writeln("Remove an user : " + user);		
		flow.setDriver(driver);
		
		try {
			
			//WebDriverWait wait = new WebDriverWait(driver, 5);
			driver.get(allUsers);
			//wait.until(ExpectedConditions.titleContains("All users"));

			searchQuery.set(user);
			searchBtn.click();
			dropdownMenuBtn.click();
			deleteUserBtn.click();
			confirmDeleteUserBtn.click();
			
			Thread.sleep(5000);
			flow.waitForHtmlHash(By.cssSelector("td.no-results"));
			
		} catch (TestFailedException e) {
			throw new TestFailedException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new TestFailedException();
		}

	}

	public void removeCampaign() {
		
		ACTION.writeln("Remove campaign");		
		flow.setDriver(driver);
		
		try {
			
			searchQuery.set("The day before you came");
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
}

package agency.july.flow;

import static agency.july.logger.Logevent.*;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import agency.july.config.models.Accesses;
import agency.july.config.models.BrowserProps;
import agency.july.config.models.Configuration;
import agency.july.webelements.Element;
import agency.july.webelements.TextInput;

public class Admin extends User {
	
	private final String baseurl = Accesses.getUrls().get("adminbase");
	private final String editUser43 = baseurl + "/?entity=User&action=edit&id=43";

	// Explore page
	private Element menuAdministration;
	
	// Admin page
	private TextInput searchQuery;	
	private Element searchBtn;
	private Element searchResults;
	private Element dropdownMenuBtn;	
	private Element deleteItemBtn;	
	private Element confirmDeleteItemBtn;
	private Element detectedItem;
	private Element allCampaignsMnu;
	private Element allUsersMnu;
	private Element allOrdersMnu;
	
	// Moderation page	    
	private Element acceptedModeration;	
	private Element saveChangesModeration;
	private TextInput campaignModerationComment;

	public Admin(Flow flow, BrowserProps props) {
		super(flow, props);
	    
		// Explore page	    
	    menuAdministration = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("explorepage").get("menuAdministration")));
	    menuAdministration.setFirstWait(160);
		// Admin page	    
		searchQuery = new TextInput(this.flow, By.cssSelector(Configuration.getCsss().get("adminpage").get("searchQuery")));	
		searchBtn = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("adminpage").get("searchBtn")));	
		searchResults = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("adminpage").get("searchResults"))); 
		dropdownMenuBtn = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("adminpage").get("dropdownMenuBtn")));	
		deleteItemBtn = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("adminpage").get("deleteItemBtn")));	
		confirmDeleteItemBtn = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("adminpage").get("confirmDeleteItemBtn")));
		detectedItem = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("adminpage").get("detectedItem")));
		allCampaignsMnu = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("adminpage").get("allCampaignsMnu")));
		allUsersMnu = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("adminpage").get("allUsersMnu")));
		allOrdersMnu = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("adminpage").get("allOrdersMnu")));
		// Moderation page	    
		acceptedModeration = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("moderationpage").get("acceptedModeration")));
		saveChangesModeration = new Element(this.flow, By.cssSelector(Configuration.getCsss().get("moderationpage").get("saveChangesModeration")));
		campaignModerationComment = new TextInput(this.flow, By.cssSelector(Configuration.getCsss().get("moderationpage").get("campaignModerationComment")));
	}
	
	public Admin withUser(String user) {
		this.userEmail = Accesses.getLogins().get(user);
		this.userPasswd = Accesses.getPasswds().get(user);
		this.userFullName = Accesses.getUsernames().get(user);
		String[] fullName = this.userFullName.split(" +");
		this.userFirstName = fullName[0];
		this.userLastName = fullName[1];
		this.userTel = "+380502222222";
		return this;
	}
	
	private void searhItems(Element menu, String searchLine) {
		menu.click();
		searchQuery.set(searchLine);
		searchBtn.click();
	}
	
	private String[] getItemIds(Element menu, String searchLine) {
		
		String[] result = null;
		
		searhItems(menu, searchLine);
		
		if ( !searchResults.getText().contains("No results found") ) {
			List<WebElement> campaigns = driver.findElements(detectedItem.getBy());
			result = new String[campaigns.size()];
			for (int i = 0; i<result.length; i++) {
				result[i] = campaigns.get(i).getAttribute("data-id");
			}
			return result;
		}		
		return new String[0];
	}
	
	private void removeItems(Element menu, String searchLine) {
		
		searhItems(menu, searchLine);
		
		while ( !searchResults.getText().contains("No results found") ) {
			dropdownMenuBtn.click();
			deleteItemBtn.click();
			confirmDeleteItemBtn.click();
			searchBtn.click();
		}		
	}
	
    @Override
	public String getBaseUrl() {
    	return baseurl;
    }
    
    public String getCampaignId(String campaignTitle) {
    	
		ACTION.writeln("Get campaign ID by title : " + campaignTitle);		
		flow.setDriver(driver);

		String campaignId = "";
		
		try {
			searhItems(allCampaignsMnu, campaignTitle);
			
			if ( !searchResults.getText().contains("No results found") ) {
				campaignId = detectedItem.getAttr("data-id");
				PASSED.writeln("Campaign ID was gotten. Id = " + campaignId);
			} else {
				FAILED.writeln("Campaign ID was not gotten");
			}			
		} catch (TestFailedException e) {
			throw new TestFailedException();			
		} catch (Exception e) {
			FAILED.writeln("Campaign ID was not gotten");
			e.printStackTrace();
			throw new TestFailedException();
		}
    	return campaignId; 
    }

	public void navigateToAdminPage() {
		
		ACTION.writeln("Navigate to admin page : " + this.userEmail);		
		flow.setDriver(driver);

		WebDriverWait wait = new WebDriverWait(driver, 5);
		
		try {
			profileBtn.click();
			menuAdministration.click();
			// Change window
			for( String winHandle : driver.getWindowHandles() ) {
			    driver.switchTo().window(winHandle);
			}
			
	    	Dimension dim = new Dimension( 1200, 800 );
	    	driver.manage().window().setSize( dim );
			wait.until(ExpectedConditions.titleContains("Dashboard"));
			
		} catch (TestFailedException e) {
			throw new TestFailedException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new TestFailedException();
		}
	}
	
	public void navigateToCampaign(String campaignId) {
		
		ACTION.writeln("Admin navigates to campaign #" + campaignId);		
		flow.setDriver(driver);
		
		String cssS = "tr[data-id='{id}']>td[data-label='Title']>a".replace("{id}", campaignId);
		Element campaign = new Element(this.flow, By.cssSelector (cssS));
		
		allCampaignsMnu.click();
		campaign.click();
	}
	
	public void sendMoney(String campaignId, float amount) {
		
		ACTION.writeln("Send money to creator for campaign: #" + campaignId);		
		flow.setDriver(driver);
		
		navigateToCampaign(campaignId);
		
		driver.findElement(By.xpath("//*[@id='main']/div[1]/ul/li[5]/a")).click();
		flow.makeScreenshot("A");
		
		TextInput amountIn = new TextInput(this.flow, By.cssSelector ("input#form_amount"));
				
		float maxAmount = Float.parseFloat( amountIn.getAttr("value") );
		
		if (amount < maxAmount) {
			amountIn.set(Float.toString(amount));
		}
		flow.makeScreenshot("B");
		
		driver.findElement(By.cssSelector("#form_save")).click();
		flow.sleep(1000);
		flow.makeScreenshot("C");
		
	}
	
	public void setModeratorRole43(boolean tick) {
		ACTION.writeln("Set moderation role: " + tick);		
		flow.setDriver(driver);
		
		try {
			
			WebDriverWait wait = new WebDriverWait(driver, 5);
			driver.get(editUser43);
			wait.until(ExpectedConditions.titleContains("Edit User (#43)"));
			
			WebElement checkboxModerator = driver.findElement(By.cssSelector("input[value='ROLE_MODERATOR']"));
			if ( tick && checkboxModerator.getAttribute("checked") == null ) checkboxModerator.click();
			if ( !tick && checkboxModerator.getAttribute("checked") != null ) checkboxModerator.click();
			driver.findElement(By.cssSelector("button.action-save")).click();		
		} catch (TestFailedException e) {
			throw new TestFailedException();
		} catch (Exception e) {
			flow.makeErrorScreenshot();
			e.printStackTrace();
			throw new TestFailedException();
		}
	}
		
	public void removeOrders(String campaignSearch) {
		
		ACTION.writeln("Remove all orders of campaigns with words in Search line '" + campaignSearch + "'");		
		flow.setDriver(driver);
		
		try {
			
			String[] campaignId = this.getCampaignIds(campaignSearch);
			for (int i=0; i<campaignId.length; i++) {
//				System.out.println(campaignId[i]);
				removeCampaignOrders(campaignId[i]);
			}
			
		} catch (TestFailedException e) {
			throw new TestFailedException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new TestFailedException();
		}
	}
	
	public String[] getCampaignIds(String searchLine) {
		
		ACTION.writeln("Get all campaign Ids with words in the searsh line '" + searchLine + "'");		
		flow.setDriver(driver);
		
		return getItemIds(allCampaignsMnu, searchLine);
		
	}
	
	public String[] getUserIds(String searchLine) {
		
		ACTION.writeln("Get all user Ids with words in the searsh line '" + searchLine + "'");		
		flow.setDriver(driver);
		
		return getItemIds(allUsersMnu, searchLine);
		
	}

	public void removeUsers(String searchLine) {
		
		ACTION.writeln( "Remove all users. Search line '" + searchLine + "'" );		
		flow.setDriver(driver);
		
		removeItems(allUsersMnu, searchLine);
	}
	
	public void removeCampaigns(String searchLine) {
		
		ACTION.writeln( "Remove all campaigns. Search line '" + searchLine + "'" );		
		flow.setDriver(driver);
		
		removeItems(allCampaignsMnu, searchLine);
	}
	
	public void removeCampaignOrders(String campaignId) {
		
		ACTION.writeln( "Remove all orders of campaign #" + campaignId );		
		flow.setDriver(driver);
		
		boolean end;
		allOrdersMnu.click();
		do {
			end = false;
	
			List<WebElement> rows = driver.findElements(By.cssSelector("tbody>tr"));
			
			for(int i = 0; i<rows.size(); i++) {
				WebElement foundCampaign = rows.get(i).findElement(By.cssSelector("td[data-label=Campaign] > a"));
				if ( foundCampaign.getText().equals("Campaign #" + campaignId) ) {
					WebElement deleteBtn = rows.get(i).findElement(By.cssSelector(".text-danger.action-delete"));
					deleteBtn.click();
					confirmDeleteItemBtn.click();
					end = true;
					break;
				}			
			}
		} while (end);
	}

	public void acceptCampaignByEmail(String linkCssSelector, String campaignName) {
		flow.setDriver(driver);
		ACTION.writeln("Accept a campaign by email. Campaign name '" + campaignName + "'");

		// Check email
		try {
			String moderationUrl = getLinkFromEmail(
				Accesses.getLogins().get("noreply"),	// from
				this.userEmail,							// to
				linkCssSelector, // confirm link in the letter
				campaignName // The email contents it
			);
			
			driver.get( moderationUrl );
			
			WebDriverWait wait = new WebDriverWait(driver, 10);
			try {
				wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("form#edit-campaign-form")));			
				PASSED.writeln("Campaign moderation form was reached");				
			} catch (TimeoutException e) {
				FAILED.writeln("Campaign moderation form wasn't reached");				
				flow.makeErrorScreenshot();
			}
		} catch (WebDriverException | InterruptedException e) {
			FAILED.writeln("The URL for moderation a new campaign is wrong");
			flow.makeErrorScreenshot();
		}
		acceptedModeration.click();
		saveChangesModeration.click();
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
			if (status == CompaignStatus.DECLINE) 
				campaignModerationComment.set("Test decline message");
			saveChangesModeration.click();
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

package agency.july.testapp;

import static agency.july.logger.Logevent.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.yaml.snakeyaml.Yaml;

import agency.july.config.models.Accesses;
import agency.july.config.models.BrowserProps;
import agency.july.config.models.Configuration;
import agency.july.flow.Admin;
import agency.july.flow.Flow;
import agency.july.flow.GoogleUser;
import agency.july.flow.ReleaseStatus;
import agency.july.flow.TestFailedException;
import agency.july.flow.User;
import agency.july.flow.CompaignStatus;
import agency.july.flow.FacebookUser;
import agency.july.logger.TestingLogger;
import agency.july.sendmail.ImapClient;

public class App {

	public static Configuration config;
	public static Accesses accesses;

	public static void main( String[] args ) throws IOException {
		
		Yaml yaml = new Yaml();
		
		long startTime = System.currentTimeMillis();
		
	    Admin admin = null;
	    Admin root = null;
		try {
			
			BufferedReader br = new BufferedReader(new FileReader(new File("./thispath.txt")));
			String thispath = br.readLine();
			br.close();
	        System.out.println("thispath : " + thispath);
			 
			config = yaml.loadAs( new FileInputStream (new File(thispath + "params.yml")), Configuration.class );
			accesses = yaml.loadAs( new FileInputStream (new File(thispath + "insecure.yml")), Accesses.class );
	        System.out.println(config.toString());
	        System.out.println(accesses.toString());
	        
			new TestingLogger(Paths.get(Accesses.getPathto().get("logfile")));
			
			DEBUG.setEnable(Configuration.getLogger().get("debug"));
			ACTION.setEnable(Configuration.getLogger().get("action"));
			INFO.setEnable(Configuration.getLogger().get("info"));
			WARNING.setEnable(Configuration.getLogger().get("warning"));
			PASSED.setEnable(Configuration.getLogger().get("passed"));
			FAILED.setEnable(Configuration.getLogger().get("failed"));
			
			// Browsers for testing
			BrowserProps descTopBrowser = Configuration.getBrowsers().get("DescTop");
			BrowserProps iPhone6Browser = Configuration.getBrowsers().get("IPhone6");
			
			// Predefined conditions
			// To make sure that there are no unread emails
		    ImapClient imapClient = new ImapClient (Accesses.getEmail());
		    imapClient.markAsSeen(Accesses.getLogins().get("noreply"));
		    
		    admin = new Admin( new Flow( "befor_after_testing" ), descTopBrowser ).withUser( "admin" );
			admin.login();
			admin.navigateToAdminPage();
//			admin.navigateToCampaign("389");
//			admin.sendMoney("389", 100f);
			admin.setModeratorRole43(true); // Set moderator role for admin
		    root = new Admin( new Flow( "befor_after_testing" ), descTopBrowser ).withUser( "root" );
		    root.login();
		    root.navigateToAdminPage();
		    root.removeUsers("temporary");		    
		    root.removeOrders("Temporary19235");
		    root.removeCampaigns("Temporary19235");
		    
			// Определяем потоки выполнения. Каждый тест в своем потоке
			Thread threadLoginLogout = null;
			Thread threadRegisterWithEmail = null;
			Thread threadStartcampaign = null;
			Thread threadReleaseinformation = null;
			Thread threadIncorrectcard = null;
			Thread threadReleaseCampaign = null;
			
			// Основное цикл по тестам
			List <String> runningTests = Configuration.getRuntests();
			if (runningTests != null)
			for (int i = 0; i < runningTests.size(); i++) {
				
				switch (runningTests.get(i)) {
				
					case "payouts" : 
						Flow flow = new Flow( "payouts" );
						User user = new User( flow, iPhone6Browser ).withUser( "campaigncreator1" );
					try {
						user.login();
						user.checkPayoutsBalance(1200);
					} catch (Exception e) {
						user.teardown();
					}
						
					break;
				
					case "loginlogout" : 
				
					threadLoginLogout = new Thread (new Runnable() {
						public void run() {
							
							Flow flow = new Flow( "loginlogout" );
							User euser = new User( flow, iPhone6Browser ).withUser( "test" );
							User guser = new GoogleUser( flow, iPhone6Browser ).withUser( "google_account" );
							User fuser = new FacebookUser( flow, iPhone6Browser ).withUser( "fb_account" );
							User currentUser = euser;
								
							try {			
								euser.login();
								euser.logout();
								
								currentUser = guser;
								guser.login();
								guser.logout(); 

// /							currentUser = fuser;
								fuser.login();
								fuser.logout(); 

								currentUser = guser;
								guser.login();
								guser.logout(); 

								currentUser = fuser;
								fuser.login();
								
								currentUser = guser;
								guser.login();
								
							} catch (TestFailedException e) {
								flow.makeErrorScreenshot();
								FAILED.writeln("Login or logout user with email '" + currentUser.getUserEmail() + "' was failed. Flow name:'" + flow.getFlowName());
							} catch (Exception e) {
								flow.makeErrorScreenshot();
								FAILED.writeln("Login or logout user with email '" + currentUser.getUserEmail() + "' was failed. Flow name:'" + flow.getFlowName());
								e.printStackTrace();
							} finally {
					            INFO.writeln("Login and logout user test finished");
					            euser.teardown();
					            guser.teardown();
					            fuser.teardown();
							}
						}
					});
					threadLoginLogout.setName("LoginLogout");
					threadLoginLogout.start();
					break;
					
					case "registerwithemail" : 
						
						threadRegisterWithEmail = new Thread (new Runnable() {
						public void run() {
							Flow flow = new Flow("registerwithemail");
				            User newuser = new User( flow, iPhone6Browser ).withUser( "newuser" );
							try {
								newuser.register();
					            newuser.checkEmailLink("a.confirm-registration", "page-account-check"); // Confirm registration
							} catch (TestFailedException e) {
								flow.makeErrorScreenshot();
								FAILED.writeln("Registration with email '" + newuser.getUserEmail() + "' has been failed. Flow name:'" + flow.getFlowName() + "'. Current slide #" + flow.getCurrentSlideNumber());
							} catch (Exception e) {
								flow.makeErrorScreenshot();
								FAILED.writeln("Registration with email '" + newuser.getUserEmail() + "' has been failed. Flow name:'" + flow.getFlowName() + "'. Current slide #" + flow.getCurrentSlideNumber());
								e.printStackTrace();
							} finally {
					            INFO.writeln("Registration with email test finished");
					            newuser.teardown();
							}
						}
					});
					threadRegisterWithEmail.setName("RegisterWithEmail");
					threadRegisterWithEmail.start();
					break;
					
					case "startcampaign" : 
						
					threadStartcampaign = new Thread (new Runnable() {
						public void run() {
							
							Flow flow = new Flow( "startcampaign" );

							User nonameUser = new User( flow, iPhone6Browser );
							nonameUser.goHome();
							User user = new User( flow, iPhone6Browser ).withUser( "test" );
				            User newuser = new User( flow, iPhone6Browser ).withUser( "newuser_startcampaign" );
				            Admin admin = new Admin( flow, iPhone6Browser ).withUser( "admin" );
							
							try {			
					            newuser.register();
					            
								admin.login();
								admin.navigateToAdminPage();

					            newuser.checkEmailLink("a.confirm-registration", "page-account-check"); // Confirm registration

					            user.login();
					            user.startCampaign(Configuration.getPatterns().get(2));
					            
					            admin.acceptCampaignByEmail("a.inform-campaign-to-moderate", Configuration.getPatterns().get(2));
					            
					            String campaignId = admin.getCampaignId(Configuration.getPatterns().get(2).split(" ")[1]);
					            
					            nonameUser.checkCampaignInList(campaignId);
					            
					            newuser.login();
					            newuser.buyCorites (campaignId, 100); // With canceling for new users

					            user.buyCorites (campaignId, 100);
					            
					            user.checkEmailLink("a.inform-creator-about-accept", "page-explore-view explore-full>article");
					            user.checkEmailLink( "a[data-e2e=informBackerAboutFunded]", "page-explore-list" );
					            user.checkEmailLink( "a[data-e2e=informCreatorAboutFunded]", "page-explore-view explore-full>article" );
					            
					            // Checking Sending money and Payouts balance
//								admin.navigateToCampaign(campaignId);
//								admin.sendMoney(campaignId, 100f);
//								user.checkPayoutsBalance(1200);

							} catch (TestFailedException e) {
								flow.makeErrorScreenshot();
								FAILED.writeln("Start Campaign flow has been failed. Flow name:'" + flow.getFlowName() + "'. Current slide #" + flow.getCurrentSlideNumber());
							} catch (Exception e) {
								flow.makeErrorScreenshot();
								FAILED.writeln("Start Campaign flow has been failed. Flow name:'" + flow.getFlowName() + "'. Current slide #" + flow.getCurrentSlideNumber());
								e.printStackTrace();
							} finally {
					            INFO.writeln("Start Campaign test finished");
					            user.teardown();
					            newuser.teardown();
					            admin.teardown();
					            nonameUser.teardown();
							}
						}
					});
					threadStartcampaign.setName("StartCampaign");
					threadStartcampaign.start();
					break;
					
					case "releasecampaign" : 
						
					threadReleaseCampaign = new Thread (new Runnable() {
						public void run() {
								
							Flow flow = new Flow( "releasecampaign" );
	
							User creator = new User( flow, iPhone6Browser ).withUser( "campaigncreator1" );
							User backer = new User( flow, iPhone6Browser ).withUser( "campaignbacker1" );
							User backerTwo = new User( flow, iPhone6Browser ).withUser( "campaignbacker2" );
					        Admin admin = new Admin( flow, descTopBrowser ).withUser( "admin" );
								
							try {			
								admin.login();
								admin.navigateToAdminPage();
					            
					            creator.login();
					            creator.startCampaign(Configuration.getPatterns().get(1));
					            String campaignId = admin.getCampaignId(Configuration.getPatterns().get(1).split(" ")[1]);
					            
					            admin.acceptCampaignByEmail("a.inform-campaign-to-moderate", Configuration.getPatterns().get(1));
					            
					            creator.checkEmailLink("a.inform-creator-about-accept", "page-explore-view explore-full>article");

					            creator.fillReleaseInfo(campaignId);
					            
					            backer.login();
					            backer.buyCorites (campaignId, 50);
					            
					            backerTwo.login();
					            backerTwo.buyCorites (campaignId, 80);
					            
					            creator.buyCorites (campaignId, 100);

					            backer.checkEmailLink( "a[data-e2e=informBackerAboutFunded]", "page-explore-list" );
					            backerTwo.checkEmailLink( "a[data-e2e=informBackerAboutFunded]", "page-explore-list" );
					            creator.checkEmailLink( "a[data-e2e=informBackerAboutFunded]", "page-explore-list" );
					            creator.checkEmailLink( "a[data-e2e=informCreatorAboutFunded]", "page-explore-view explore-full>article" );
					            
					            creator.makeReadyToRelease(campaignId);
					            
					            admin.acceptCampaignByEmail("a.inform-campaign-to-moderate", Configuration.getPatterns().get(1));
					            					            
					            creator.checkEmailLink("a[data-e2e=informCreatorAboutApproveReleaseInfo]", "page-explore-view explore-full>article");
					            
					            admin.setReleaseStatus(campaignId, ReleaseStatus.RELEASED);
					            
					            backer.checkEmailLink("a[data-e2e=informBackerAboutReleased]", "page-explore-list");
					            backerTwo.checkEmailLink("a[data-e2e=informBackerAboutReleased]", "page-explore-list");
					            creator.checkEmailLink("a[data-e2e=informBackerAboutReleased]", "page-explore-list");
					            creator.checkEmailLink("a[data-e2e=informCreatorAboutReleased]", "page-portfolio");
					            
					            creator.navigateToPortfolio();
					            creator.checkPortfolioList( Configuration.getPatterns().get(1) );
					            creator.checkMyCorites( 1636 );
					            
//					            flow.makeScreenshot("Creator");

					            backer.navigateToPortfolio();
					            backer.checkPortfolioList( Configuration.getPatterns().get(1) );
					            backer.checkMyCorites( 90 );

//					            flow.makeScreenshot("Backer");

					            backerTwo.navigateToPortfolio();
					            backerTwo.checkPortfolioList( Configuration.getPatterns().get(1) );
					            backerTwo.checkMyCorites( 74 );
//					            flow.makeScreenshot("BackerTwo");
						            
							} catch (TestFailedException e) {
								flow.makeErrorScreenshot();
								FAILED.writeln("Release Campaign flow was failed. Flow name:'" + flow.getFlowName() + "'. Current slide #" + flow.getCurrentSlideNumber());
							} catch (Exception e) {
								flow.makeErrorScreenshot();
								FAILED.writeln("Release Campaign flow was failed. Flow name:'" + flow.getFlowName() + "'. Current slide #" + flow.getCurrentSlideNumber());
								e.printStackTrace();
							} finally {
					            INFO.writeln("Release Campaign test finished");
					            creator.teardown();
					            backer.teardown();
					            backerTwo.teardown();
					            admin.teardown();
							}
						}
					});
					threadReleaseCampaign.setName("ReleaseCampaign");
					threadReleaseCampaign.start();
					break;
					
					case "releaseinformation" : 
					threadReleaseinformation = new Thread (new Runnable() {
						public void run() {
							
							Flow flow = new Flow( "releaseinformation" );
							User user = new User( flow, iPhone6Browser ).withUser( "test" );
							Admin admin = new Admin( flow, descTopBrowser ).withUser( "admin" );
							
							try {			
								
					            // Prepare initial state
								admin.login();
								admin.navigateToAdminPage();
					            admin.moderateCampaign("295", CompaignStatus.NONE);

					            // Begin test
					            user.login();
					            user.fillReleaseInfo("295");
					            
					            flow.sleep(1000); // Waiting for changing status of the campaign in database
					            
					            if ( admin.getCampaignStatus("295") == CompaignStatus.NEEDS_MODERATION ) 
					            	PASSED.writeln("Release information of campaign #295 was filled in by '" + user.getUserEmail() + "'");
					            else
					            	throw new TestFailedException();
					            
					            // Restore initial state
					            admin.moderateCampaign("295", CompaignStatus.DECLINE);
					            
					            user.checkDeclineEmail();
					            
							} catch (TestFailedException e) {
								flow.makeErrorScreenshot();
								FAILED.writeln("Filling in a release information of campaign #295 by '" + user.getUserEmail() + "' was failed. Flow name:'" + flow.getFlowName() + "'. Current slide #" + flow.getCurrentSlideNumber());
							} catch (Exception e) {
								flow.makeErrorScreenshot();
								FAILED.writeln("Filling in a release information '" + user.getUserEmail() + "' was failed. Flow name:'" + flow.getFlowName() + "'. Current slide #" + flow.getCurrentSlideNumber());
								e.printStackTrace();
							} finally {
					            INFO.writeln("Filling in a release information test finished");
					            user.teardown();
					            admin.teardown();
							}
						}
					});
					threadReleaseinformation.setName("ReleaseInformation");
					threadReleaseinformation.start();
					break;
						
					case "incorrectcard" :
					threadIncorrectcard = new Thread (new Runnable() {
						public void run() {
							
							Flow flow = new Flow( "incorrectcard" );
							User user = new User( flow, iPhone6Browser ).withUser( "incorrectcard" );
							
							try {			

					            user.login();
					            String campaignId = Configuration.getPatterns().get(3); // Predefined campaign
					            user.checkIncorrectCard(campaignId, "4242424242424242"); // Проходит Ok
					            user.checkIncorrectCard(campaignId, "4242424242424241"); // Не проходит Ok
					            user.checkIncorrectCard(campaignId, "4000000000000119"); // Проходит, а не должно
					            user.checkIncorrectCard(campaignId, "4000000000000069"); // Проходит, а не должно
					            user.checkIncorrectCard(campaignId, "4000000000000127"); // Проходит, а не должно
					            							            
							} catch (TestFailedException e) {
								FAILED.writeln("Test of payment with incorrect card was failed. Flow name:'" + flow.getFlowName() + "'. Current slide #" + flow.getCurrentSlideNumber());
								flow.makeErrorScreenshot();
							} catch (Exception e) {
								flow.makeErrorScreenshot();
								FAILED.writeln("Test of payment with incorrect card was failed. Flow name:'" + flow.getFlowName() + "'. Current slide #" + flow.getCurrentSlideNumber());
								e.printStackTrace();
							} finally {
					            INFO.writeln("Test of payment with incorrect card was finished");
					            user.teardown();
							}
						}
					});
					threadIncorrectcard.setName("IncorrectCard");
					threadIncorrectcard.start();
					break;
				}
			}
			// Ожидание выполнения всех потоков (тестов)
			if (threadLoginLogout != null) threadLoginLogout.join();
			if (threadRegisterWithEmail != null) threadRegisterWithEmail.join();
			if (threadStartcampaign != null) threadStartcampaign.join();
			if (threadReleaseCampaign != null) threadReleaseCampaign.join();
			if (threadReleaseinformation != null) threadReleaseinformation.join();
			if (threadIncorrectcard != null) threadIncorrectcard.join();
				
			
            // Reset moderator role for admin
            admin.setModeratorRole43(false);
            admin.teardown();
            root.removeUsers("temporary");
		    root.removeOrders("Temporary19235");
		    root.removeCampaigns("Temporary19235");
            root.teardown();

		} catch (FileNotFoundException e1) {
			System.out.println("Could not load a configuration file: './params.yml' or './insecure.yml'");
    		e1.printStackTrace();
        } catch (Exception e) {
    		e.printStackTrace();
        } finally {
            admin.teardown();
            root.teardown();
        	if (TestingLogger.output != null) TestingLogger.output.close();
        	System.out.println("----------------- Conclusion ---------------");
        	System.out.println( "In total " + ( FAILED.getCount() + PASSED.getCount() ) + " tests were performed");
        	System.out.println( FAILED.getCount() + " tests have been failed");
        	System.out.println( PASSED.getCount() + " tests have been passed");
        	System.out.println( WARNING.getCount() + " warnings");
        	
        	long duration = System.currentTimeMillis() - startTime;
        	long millis = duration % 1000;
        	long second = (duration / 1000) % 60;
        	long minute = (duration / (1000 * 60)) % 60;
        	long hour = (duration / (1000 * 60 * 60)) % 24;

        	String time = String.format("%02d:%02d:%02d.%d", hour, minute, second, millis);

        	System.out.println( "Executing time (hh:mm:ss:mls): " + time);
        	
        	System.runFinalization();
        }	        
    }
}

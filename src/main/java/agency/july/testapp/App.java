package agency.july.testapp;

import static agency.july.logger.Logevent.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.yaml.snakeyaml.Yaml;

import agency.july.config.models.Accesses;
import agency.july.config.models.Configuration;
import agency.july.flow.Admin;
import agency.july.flow.Flow;
import agency.july.flow.GoogleUser;
import agency.july.flow.ReleaseStatus;
import agency.july.flow.Test;
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
		
			try {
				config = yaml.loadAs( new FileInputStream (new File("./params.yml")), Configuration.class );
				accesses = yaml.loadAs( new FileInputStream (new File("./insecure.yml")), Accesses.class );
		        System.out.println(config.toString());
		        System.out.println(accesses.toString());
				new TestingLogger(Paths.get(Accesses.getPathto().get("logfile")));
				
				DEBUG.setEnable(Configuration.getLogger().get("debug"));
				ACTION.setEnable(Configuration.getLogger().get("action"));
				INFO.setEnable(Configuration.getLogger().get("info"));
				WARNING.setEnable(Configuration.getLogger().get("warning"));
				PASSED.setEnable(Configuration.getLogger().get("passed"));
				FAILED.setEnable(Configuration.getLogger().get("failed"));
				
				// To make sure that there are no unread emails
			    ImapClient imapClient = new ImapClient (Accesses.getEmail());
			    imapClient.markAsSeen(Accesses.getLogins().get("noreply"));
				
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
					
						case "loginlogout" : 
					
						threadLoginLogout = new Thread (new Runnable() {
							public void run() {
								
								Flow flow = new Flow( "loginlogout" );
								User euser = new User( flow ).withUser( "test" );
								User guser = new GoogleUser( flow ).withUser( "google_account" );
								User fuser = new FacebookUser( flow ).withUser( "fb_account" );
								User currentUser = euser;
								
								try {			
/*									
									euser.login();
									euser.logout();
									
									currentUser = guser;
									guser.login();
									guser.logout(); 

*/									currentUser = fuser;
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
									flow.makeScreenshot();
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
					            User newuser = new User( flow ).withUser( "newuser" );
					            Admin root = new Admin( flow ).withUser( "root" );
								try {
									root.login(); //LoginMode.EMAIL
									root.gotoAdminPage();
						            root.removeUser( newuser.getUserFullName() ); // Remove the newuser if he exists
									newuser.register();
									root.removeUser( newuser.getUserFullName() );
								} catch (TestFailedException e) {
									flow.makeScreenshot();
									FAILED.writeln("Registration with email '" + newuser.getUserEmail() + "' has been failed. Flow name:'" + flow.getFlowName() + "'. Current slide #" + flow.getCurrentSlideNumber());
								} catch (Exception e) {
									flow.makeScreenshot();
									FAILED.writeln("Registration with email '" + newuser.getUserEmail() + "' has been failed. Flow name:'" + flow.getFlowName() + "'. Current slide #" + flow.getCurrentSlideNumber());
									e.printStackTrace();
								} finally {
						            INFO.writeln("Registration with email test finished");
						            newuser.teardown();
						            root.teardown();
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

					            Test nonameUser = new User( flow );
								User user = new User( flow ).withUser( "test" );
					            User newuser = new User( flow ).withUser( "newuser_startcampaign" );
					            Admin root = new Admin( flow ).withUser( "root" ); // Supper admin
					            Admin admin = new Admin( flow ).withUser( "admin" );
								
								try {			
						            root.login(); //LoginMode.EMAIL
									root.gotoAdminPage();
						            root.removeUser( newuser.getUserFullName() );

						            newuser.register();
						            
									admin.login(); //LoginMode.EMAIL
						            admin.setModeratorRole43(true);
						            
						            user.login(); //LoginMode.EMAIL
						            user.startCampaign();
						            
						            admin.acceptCampaignByEmail();
						            admin.setModeratorRole43(false);
						            
						            nonameUser.checkFirstCampaignInList();

						            String campaignId = admin.getCampaignId(Configuration.getPatterns().get(1));
						            
//						            newuser.login();
						            newuser.buyCorites (campaignId); // With canceling for new users

						            user.buyCorites (campaignId);
						            
						            root.removeUser( newuser.getUserFullName() );
//*						            
									root.removeCampaignOrders( campaignId );
									root.removeCampaignByName(Configuration.getPatterns().get(1));
//*/						            
								} catch (TestFailedException e) {
									flow.makeScreenshot();
									FAILED.writeln("Start Campaign flow has been failed. Flow name:'" + flow.getFlowName() + "'. Current slide #" + flow.getCurrentSlideNumber());
								} catch (Exception e) {
									flow.makeScreenshot();
									FAILED.writeln("Start Campaign flow has been failed. Flow name:'" + flow.getFlowName() + "'. Current slide #" + flow.getCurrentSlideNumber());
									e.printStackTrace();
								} finally {
						            INFO.writeln("Start Campaign test finished");
						            user.teardown();
						            newuser.teardown();
						            root.teardown();
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

								User creator = new User( flow ).withUser( "campaigncreator1" );
								User backer = new User( flow ).withUser( "campaignbacker1" );
					            Admin root = new Admin( flow ).withUser( "root" ); // Supper admin
					            Admin admin = new Admin( flow ).withUser( "admin" );
								
								try {			
						            root.login();
									root.gotoAdminPage();

									admin.login();
									admin.gotoAdminPage();
						            admin.setModeratorRole43(true);
						            
						            creator.login();
						            creator.startCampaign();
						            String campaignId = admin.getCampaignId(Configuration.getPatterns().get(1));
						            
						            admin.acceptCampaignByEmail();
						            
						            creator.checkEmailLink("a.inform-creator-about-accept", "page-explore-view explore-full>article");

						            creator.fillReleaseInfo(campaignId);
						            
						            backer.login();
						            backer.buyCorites (campaignId);
						            
						            backer.checkEmailLink( "a[data-e2e=informBackerAboutFunded]", "page-explore-list" );
						            creator.checkEmailLink( "a[data-e2e=informCreatorAboutFunded]", "page-explore-view explore-full>article" );
						            
						            creator.makeReadyToRelease(campaignId);
						            
						            admin.acceptCampaignByEmail();
//						            admin.moderateCampaign(campaignId, CompaignStatus.ACCEPT);
						            
						            creator.checkEmailLink("a[data-e2e=informCreatorAboutApproveReleaseInfo]", "page-explore-view explore-full>article");
						            
						            admin.setReleaseStatus(campaignId, ReleaseStatus.RELEASED);
						            
						            backer.checkEmailLink("a[data-e2e=informBackerAboutReleased]", "page-explore-list");
						            creator.checkEmailLink("a[data-e2e=informCreatorAboutReleased]", "page-portfolio");
						            
						            admin.setModeratorRole43(false);
//*						            
									root.removeCampaignOrders( campaignId );
									root.removeCampaignByName(Configuration.getPatterns().get(1));
//*/						            
								} catch (TestFailedException e) {
									flow.makeScreenshot();
									FAILED.writeln("Start Campaign flow has been failed. Flow name:'" + flow.getFlowName() + "'. Current slide #" + flow.getCurrentSlideNumber());
								} catch (Exception e) {
									flow.makeScreenshot();
									FAILED.writeln("Start Campaign flow has been failed. Flow name:'" + flow.getFlowName() + "'. Current slide #" + flow.getCurrentSlideNumber());
									e.printStackTrace();
								} finally {
						            INFO.writeln("Start Campaign test finished");
						            creator.teardown();
						            backer.teardown();
						            root.teardown();
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
									User user = new User( flow ).withUser( "test" );
									Admin admin = new Admin( flow ).withUser( "admin" );
									
									try {			
										
							            // Prepare initial state
										admin.login();
										admin.gotoAdminPage();
							            admin.moderateCampaign("295", CompaignStatus.NONE);

							            // Begin test
							            user.login(); //LoginMode.EMAIL
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
										flow.makeScreenshot();
										FAILED.writeln("Filling in a release information of campaign #295 by '" + user.getUserEmail() + "' was failed. Flow name:'" + flow.getFlowName() + "'. Current slide #" + flow.getCurrentSlideNumber());
									} catch (Exception e) {
										flow.makeScreenshot();
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
									User user = new User( flow ).withUser( "incorrectcard" );
									
									try {			

							            user.login(); //LoginMode.EMAIL
							            user.buyCoritesIncorrectCard("289");
							            							            
									} catch (Exception e) {
										flow.makeScreenshot();
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

			} catch (FileNotFoundException e1) {
				System.out.println("Could not load configuration file: ./params.yml");
	    		e1.printStackTrace();
	        } catch (Exception e) {
	    		e.printStackTrace();
	        } finally {
	        	if (TestingLogger.output != null) TestingLogger.output.close();
	        	System.out.println("----------------- Conclusion ---------------");
	        	System.out.println( "In total " + ( FAILED.getCount() + PASSED.getCount() ) + " tests were performed");
	        	System.out.println( FAILED.getCount() + " tests have been failed");
	        	System.out.println( PASSED.getCount() + " tests have been passed");
	        	System.out.println( WARNING.getCount() + " warnings");
	        }
	        
	        
    }
}

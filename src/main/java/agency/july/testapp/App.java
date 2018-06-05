package agency.july.testapp;

import static agency.july.logger.Logevent.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;

import org.yaml.snakeyaml.Yaml;

import com.gargoylesoftware.htmlunit.WebConsole.Logger;

import agency.july.config.models.Accesses;
import agency.july.config.models.Configuration;
import agency.july.flow.Admin;
import agency.july.flow.Flow;
import agency.july.flow.Test;
import agency.july.flow.TestFailedException;
import agency.july.flow.User;
import agency.july.flow.CompaignStatus;
import agency.july.logger.TestingLogger;

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
				
				// Определяем потоки выполнения. Каждый тест в своем потоке
				Thread threadLoginLogout = null;
				Thread threadRegisterWithEmail = null;
				Thread threadStartcampaign = null;
				Thread threadReleaseinformation = null;
				
				// Основное цикл по тестам
				List <String> runningTests = Configuration.getRuntests();
				if (runningTests != null) 
				for (int i = 0; i < runningTests.size(); i++) {
					
					switch (runningTests.get(i)) {
					
						case "loginlogout" : 
					
						threadLoginLogout = new Thread (new Runnable() {
							public void run() {
								
								Flow flow = new Flow( "loginlogout" );
								User user = new User( flow ).withUser( "test" );
								
								try {			
									
						            user.login(); 
						            user.logout(); 
						            
								} catch (TestFailedException e) {
									flow.makeScreenshot();
									FAILED.writeln("Login or logout user with email '" + user.getUserEmail() + "' has been failed. Flow name:'" + flow.getFlowName() + "'. Current slide #" + flow.getCurrentSlideNumber());
								} catch (Exception e) {
									flow.makeScreenshot();
									FAILED.writeln("Login or logout user with email '" + user.getUserEmail() + "' has been failed. Flow name:'" + flow.getFlowName() + "'. Current slide #" + flow.getCurrentSlideNumber());
									e.printStackTrace();
								} finally {
						            INFO.writeln("Login and logout user test finished");
						            user.teardown();
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
					            Admin admin = new Admin( flow ).withUser( "root" );
								try {
									newuser.register();
									admin.login();
								    admin.gotoAdminPage();
								    admin.removeUser( newuser.getUserFullName() );
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
						            admin.teardown();
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
						            newuser.register();
						            
									admin.login(); 
						            admin.setModeratorRole43(true);
						            
						            user.login(); 
						            user.startCampaign();
						            
						            admin.confirmModeration();
						            admin.setModeratorRole43(false);
						            
						            nonameUser.checkFirstCampaignInList();

						            String campaignId = admin.getCampaignId(Configuration.getPatterns().get(1));
						            
						            newuser.login();
						            newuser.buyCorites (campaignId); // With canceling for new users

						            user.buyCorites (campaignId);
						            
//						            user.fillReleaseInfo(campaignId);

						            root.login();
									root.gotoAdminPage();
						            root.removeUser( newuser.getUserFullName() );
//*						            
//									root.removeCampaignOrders( campaignId );
//									root.removeCampaign(Configuration.getPatterns().get(1));
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
						
						case "releaseinformation" : 
							threadReleaseinformation = new Thread (new Runnable() {
								public void run() {
									
									Flow flow = new Flow( "releaseinformation" );
									User user = new User( flow ).withUser( "test" );
									Admin admin = new Admin( flow ).withUser( "admin" );
									
									try {			
										
							            // Prepare initial state
										admin.login(); 
							            admin.moderateCampaign("295", CompaignStatus.NONE);

							            // Begin test
							            user.login(); 
							            user.fillReleaseInfo("295");
							            
							            if ( admin.getCompaignStatus("295") == CompaignStatus.NEEDS_MODERATION ) 
							            	PASSED.writeln("Release information of campaign #295 was filled in by '" + user.getUserEmail() + "'");
							            else
							            	throw new TestFailedException();
							            
							            // Restore initial state
							            admin.moderateCampaign("295", CompaignStatus.DECLINE);
							            
							            user.checkDeclineEmail();
							            
//							            user.logout(); 
							            
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

					}
				}
				
				// Ожидание выполнения всех потоков (тестов)
				if (threadLoginLogout != null) threadLoginLogout.join();
				if (threadRegisterWithEmail != null) threadRegisterWithEmail.join();
				if (threadStartcampaign != null) threadStartcampaign.join();
				if (threadReleaseinformation != null) threadReleaseinformation.join();

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

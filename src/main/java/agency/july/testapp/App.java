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
import agency.july.flow.Test;
import agency.july.flow.TestFailedException;
import agency.july.flow.User;
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
						            PASSED.writeln("Login an user");
						            user.logout(); 
						            PASSED.writeln("Logout an user");
						            
								} catch (TestFailedException e) {
									flow.makeScreenshot();
									FAILED.writeln("Login or logout user with email '" + user.getUserEmail() + "' has been failed. Flow name:'" + flow.getFlowName() + "'. Current slide #" + flow.getCurrentSlide());
								} catch (Exception e) {
									flow.makeScreenshot();
									FAILED.writeln("Login or logout user with email '" + user.getUserEmail() + "' has been failed. Flow name:'" + flow.getFlowName() + "'. Current slide #" + flow.getCurrentSlide());
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
					            User user = new User( flow ).withUser( "newuser" );
					            Admin admin = new Admin( flow ).withUser( "root" );
								try {
									user.register();
									PASSED.writeln("Registration an user");
									admin.login();
									PASSED.writeln("Login the admin");
								    admin.gotoAdminPage();
									PASSED.writeln("Admin page has been reached");
								    admin.removeUser( user.getUserFullName() );
									PASSED.writeln("User has been removed");
								} catch (TestFailedException e) {
									flow.makeScreenshot();
									FAILED.writeln("Registration with email '" + user.getUserEmail() + "' has been failed. Flow name:'" + flow.getFlowName() + "'. Current slide #" + flow.getCurrentSlide());
								} catch (Exception e) {
									flow.makeScreenshot();
									FAILED.writeln("Registration with email '" + user.getUserEmail() + "' has been failed. Flow name:'" + flow.getFlowName() + "'. Current slide #" + flow.getCurrentSlide());
									e.printStackTrace();
								} finally {
						            INFO.writeln("Registration with email test finished");
						            user.teardown();
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
					            Admin root = new Admin( flow ).withUser( "root" ); // Supper admin
					            Admin admin = new Admin( flow ).withUser( "admin" );
								
								try {			
									
									admin.login(); 
						            admin.setModeratorRole43(true);
						            
						            flow.incSlideNumber();
						            
						            user.login(); 
						            user.startCampaign();
						            
						            admin.confirmModeration();
						            admin.setModeratorRole43(false);
						            
						            nonameUser.checkFirstCampaignInList();
/*						            
						            root.login();
									root.gotoAdminPage();
									root.removeCampaign("The day before you came");
*/						            
								} catch (TestFailedException e) {
									flow.makeScreenshot();
									FAILED.writeln("Start Campaign flow has been failed. Flow name:'" + flow.getFlowName() + "'. Current slide #" + flow.getCurrentSlide());
								} catch (Exception e) {
									flow.makeScreenshot();
									FAILED.writeln("Start Campaign flow has been failed. Flow name:'" + flow.getFlowName() + "'. Current slide #" + flow.getCurrentSlide());
									e.printStackTrace();
								} finally {
						            INFO.writeln("Start Campaign test finished");
						            user.teardown();
						            root.teardown();
						            admin.teardown();
								}
							}
						});
							threadStartcampaign.setName("StartCampaign");
							threadStartcampaign.start();
						break;

					}
				}
				
				// Ожидание выполнения всех потоков (тестов)
				if (threadLoginLogout != null) threadLoginLogout.join();
				if (threadRegisterWithEmail != null) threadRegisterWithEmail.join();
				if (threadStartcampaign != null) threadStartcampaign.join();

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

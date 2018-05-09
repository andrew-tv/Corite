package agency.july.logger;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class LoggerMain {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Map<String, Boolean> myMap = new HashMap<String, Boolean>();
		myMap.put("action", true);
		myMap.put("info", false);
		myMap.put("passed", true);
		myMap.put("failed", true);


		Logevent.ACTION.setEnable(myMap.get("action"));
		Logevent.INFO.setEnable(myMap.get("info"));
		Logevent.PASSED.setEnable(myMap.get("passed"));
		Logevent.FAILED.setEnable(myMap.get("failed"));
		
		new TestingLogger(Paths.get("./logfile.txt"));
		
		Logevent.ACTION.writeln("An ACTION message to log");
		Logevent.FAILED.writeln("A FAILED message to log");
		Logevent.INFO.writeln("A INFO message to log");
		Logevent.PASSED.writeln("A PASSED message to log");
//		LoggerEvent.PASSED.writeln("A PASSED message to log 2");
		
		TestingLogger.output.close();

	}

}

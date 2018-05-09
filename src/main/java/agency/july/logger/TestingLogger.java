package agency.july.logger;

import static java.nio.file.StandardOpenOption.*;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestingLogger {
	
	public static OutputStream output;
	
	public TestingLogger(Path p) throws IOException {
		output = new BufferedOutputStream (Files.newOutputStream(p, CREATE, WRITE, TRUNCATE_EXISTING));
	}
	
	static void write(byte[] data) {
		try {
			output.write(data, 0, data.length);
		} catch (IOException e) {			
			System.err.println("Error writing to log file");
			e.printStackTrace();
		}
	}
}

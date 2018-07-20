package agency.july.logger;

import java.io.PrintStream;

public enum Logevent {
	DEBUG (">>> ", false, System.out, "\u001B[34m") {
		public void writeln(String msg) { // Этот метод для DEBUG переопределен так как он никогда не выводит в лог файл (только в консоль)
			String line = Thread.currentThread().getName() + " >> " + this.prefix + msg;
			
			if (/*IDE_version*/false) { // Для IDE запуска без эскейп последовательностей так как эклипсовая консоль их не понимает
				if (this.enable) this.output.println(line);
			} else {
				if (this.enable) this.output.println(this.color + line + "\u001B[0m");			
			}
		}
	},
	INFO ("INFO >> ", true, System.out, ""),
	ACTION ("ACTION >> ", true, System.out, ""),
	PASSED ("PASSED >> ", true, System.out, "\u001B[32m"),
	WARNING ("WARNING >> ", true, System.err, "\u001B[31m"),
	FAILED ("FAILED >> ", true, System.err, "\u001B[31m\u001B[1m");
	
	final String prefix;
	Boolean enable;
	final PrintStream output;
	final String color;
	
	int counter = 0;
	
	Logevent (String prefix, Boolean enable, PrintStream output, String color) {
		this.prefix = prefix;
		this.enable = enable;
		this.output = output;
		this.color = color;
	}
	
	public void setEnable(Boolean enable) { // Устанавливает выводимость на консоль данного типа сообщения
		if (enable != null) this.enable = enable;
	}
	
	public static String getCodePoint() { // Возвращает точку кода в котором произошел вызов метода writeln()
	    StackTraceElement ste = Thread.currentThread().getStackTrace()[3]; // 3 - уровень отката по стеку вызовов функций
//	    if ( ste.getClassName().equals("agency.july.logger.Assert") ) {
//	    	ste = Thread.currentThread().getStackTrace()[5];
//	    }
	    return //ste.getFileName() + ":" + 
	           ste.getClassName() + ":" + 
	           ste.getMethodName() + "():" +  
	           ste.getLineNumber();
	}	
	
	public void writeln(String msg) { // Печатает сообщение на консоль и в log-file в соответствии с настройками. (Для DEBUG переопределен)
		String line = Thread.currentThread().getName() + " >> " + this.prefix + msg;
		if (/*IDE_version*/false) { // Для IDE запуска без эскейп последовательностей так как эклипсовая консоль их не понимает
			if (this.enable) this.output.println(line);
		} else {
			if (this.enable) this.output.println(this.color + line + "\u001B[0m");			
		}
		byte data[] = (line + " >> " + getCodePoint() + "\n").getBytes();
		TestingLogger.write(data);
		counter++;
	}
	
	public int getCount() {
		return this.counter;
	}
}

package agency.july.webelementtypes;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public enum WebElementTypes {
	BUTTON {
		public void set(WebDriver driver, Boolean[] ticks, By by) throws InvalidWebElementOperation {
			throw new InvalidWebElementOperation();
		}
		public void set(WebDriver driver, String value, By by) throws InvalidWebElementOperation {
			throw new InvalidWebElementOperation();
		}
	},
	CHECKBOXES {
		public void set(WebDriver driver, String value, By by) throws InvalidWebElementOperation {
			throw new InvalidWebElementOperation();
		}
	},
	DROPDOWN {
		public void set(WebDriver driver, Boolean[] ticks, By by) throws InvalidWebElementOperation {
			throw new InvalidWebElementOperation();
		}
		// Выбор значения в выпадающем списке
		public void set(WebDriver driver, String value, By by) throws InvalidWebElementOperation {
			Select dropList = new Select(driver.findElement(by));
			dropList.selectByValue(value);
		}
	},
	ELEMENT {
		public void set(WebDriver driver, Boolean[] ticks, By by) throws InvalidWebElementOperation {
			throw new InvalidWebElementOperation();
		}		
		public void set(WebDriver driver, String value, By by) throws InvalidWebElementOperation {
			throw new InvalidWebElementOperation();
		}
	},
	RADIOBUTTONS {
		public void set(WebDriver driver, String value, By by) throws InvalidWebElementOperation {
			throw new InvalidWebElementOperation();
		}
	},
	INPUT {
		public void set(WebDriver driver, Boolean[] ticks, By by) throws InvalidWebElementOperation {
			throw new InvalidWebElementOperation();
		}				
	};

	// Установка/снятие птичек в чекбоксах и радибаттонах
	public void set(WebDriver driver, Boolean[] ticks, By by) throws DifferentSizesOfArrays, InvalidWebElementOperation { 
		List<WebElement> boxes = driver.findElements(by);
		if ( ticks.length != boxes.size() ) throw new DifferentSizesOfArrays();
		for (int i=0; i < ticks.length; i++) { // Установка/снятие птичек в зависимости от массива ticks
			WebElement box = boxes.get(i);
			if ( (ticks[i] && !box.isSelected()) || (!ticks[i] && box.isSelected()) ) box.click();
		}
	}
	
	// Ввод текста в строку ввода
	public void set(WebDriver driver, String value, By by) throws InvalidWebElementOperation {
		driver.findElement(by).clear();
		driver.findElement(by).sendKeys(value); // Ввели текст
	}
	
	// Клик по элементу (применимо ко всем типам элемментов, поэтому исключение InvalidWebElementOperation не используется)
	public void click(WebDriver driver, By by) {
		driver.findElement(by).click(); // Нажали кнопку (кликнули по любому элементу)
	}
	
	// Получить содержимое элемента (применимо ко всем типам элемментов, поэтому исключение InvalidWebElementOperation не используется)
	public String getContent(WebDriver driver, By by) {
		return driver.findElement(by).getText();
	}
	
	// Получить значение атрибута value
	public String getValue(WebDriver driver, By by) {
		return driver.findElement(by).getAttribute("value");
	}
	
	// Получить содержимое элементов (применимо ко всем типам элемментов, поэтому исключение InvalidWebElementOperation не используется)
	public String getContents(WebDriver driver, By by) {
		List<WebElement> el_list = driver.findElements(by);
		if ( el_list.size() > 0 ) {
			StringBuilder str_list = new StringBuilder(el_list.get(0).getText());
			for (int i = 1; i<el_list.size(); i++) str_list.append("\n" + el_list.get(i).getText());
			return str_list.toString();
		} else return "";
	}
	
	// Получить значение аттрибута (применимо ко всем типам элемментов, поэтому исключение InvalidWebElementOperation не используется)
	public String getAttribute(WebDriver driver, By by, String attr) {
		return driver.findElement(by).getAttribute(attr);
	}
	
	// Получить информацию об вебэлементе
	public String getInfo(By by) {
		return by.toString();
	}

}

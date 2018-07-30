package sender;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class MailSender {
	private WebDriver driver = null;
	private WebDriverWait wait = null;
	
	private String xpathToComposeButton = "//div[text() = 'COMPOSE' and @role = 'button']";
	private String xpathToComposeDialog = "//div[@class = 'dw']//div[@class = 'nH nn']//div[@class = 'nH Hd' and @role = 'dialog']";
	private String xpathToRecipientsField = xpathToComposeDialog + "//textarea[@name = 'to']";
	private String xpathToRecipientsAutocomplete = xpathToComposeDialog
			+ "//textarea[@name = 'to' and @aria-haspopup = 'true' and @aria-expanded = 'true']";
	private String xpathToSubjectField = "//input[@name = 'subjectbox']";
	private String xpathToMessageBody = "//div[@aria-label='Message Body']";
	private String xpathToSendButton = "//div[@role = 'button' and text() = 'Send']";

	public MailSender(String pathToChromeDriverExecutableFile) {
		initDriver(pathToChromeDriverExecutableFile);
	}

	public void initDriver(String pathToChromeDriverExecutableFile) {
		System.setProperty("webdriver.chrome.driver", pathToChromeDriverExecutableFile);
		ChromeOptions options = new ChromeOptions();
		driver = new ChromeDriver(options);
		wait = new WebDriverWait(driver, 30);
	}

	public void fillEmail(String emailAddress, String recipientName, String subject, String content)
			throws InterruptedException {
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpathToComposeButton)));
		driver.findElement(By.xpath(xpathToComposeButton)).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpathToComposeDialog)));
		driver.findElement(By.xpath(xpathToRecipientsField)).sendKeys(emailAddress);
		for (WebElement autoCompleteElement : driver.findElements(By.xpath(xpathToRecipientsAutocomplete))) {
			driver.findElement(By.xpath(xpathToRecipientsField)).sendKeys(Keys.ENTER);
		}
		driver.findElement(By.xpath(xpathToSubjectField)).click();
		driver.findElement(By.xpath(xpathToSubjectField)).sendKeys(subject);
		driver.findElement(By.xpath(xpathToMessageBody)).sendKeys(content);

		// String script = "alert(document.cookie);";
		// jsExecutor(script);
		jsExecutor(injectEmailContentScript(content));
	}

	public void loginEmail(String email, String password) {
		driver.navigate().to("https://mail.google.com/");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input [@type='email']")));
		driver.findElement(By.xpath("//input [@type='email']")).sendKeys(email);
		driver.findElement(By.xpath("//div[@id='identifierNext' or @id = 'next']")).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input [@type = 'password']")));
		driver.findElement(By.xpath("//input [@type = 'password']")).sendKeys(password);
		driver.findElement(By.xpath("//div[@id='passwordNext']")).click();
	}

	public void sendMail() {
		driver.findElement(By.xpath(xpathToSendButton)).click();
	}

	public void quitDriver() {
		driver.quit();
	}

	public void jsExecutor(String script) {
		if (driver instanceof JavascriptExecutor) {
			String result = (String) ((JavascriptExecutor) driver).executeScript(script);
		} else {
			throw new IllegalStateException("This driver does not support JavaScript!");
		}
	}

	private String injectEmailContentScript(String content) {
		return "function FindByAttributeValue(attribute, value, element_type)    {"
				+ "  element_type = element_type || \"*\";" + "  var All = document.getElementsByTagName(element_type);"
				+ "  for (var i = 0; i < All.length; i++)       {"
				+ "    if (All[i].getAttribute(attribute) == value) { return All[i]; }" + "  }" + "}"
				+ "FindByAttributeValue('aria-label','Message Body','div').innerHTML = '" + content + "'";
	}

	public void writeLogSentEmail(String firstName, String email, String logFileName) {

		String logFile = System.getProperty("user.dir") + "/data/" + logFileName;

		BufferedWriter bw = null;
		FileWriter fw = null;

		try {
			File file = new File(logFile);

			// if file doesnt exists then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			fw = new FileWriter(file.getAbsoluteFile(), true);
			bw = new BufferedWriter(fw);
			bw.newLine();
			bw.write("FirstName: " + firstName + "-- Email: " + email);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
				if (fw != null) {
					fw.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}
}

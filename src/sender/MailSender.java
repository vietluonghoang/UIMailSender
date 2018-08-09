package sender;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import exceltool.WorkingWithExcel;

public class MailSender {
	private WebDriver driver = null;
	private WebDriverWait wait = null;
	private String pathToChromeDriverExecutableFile = "";
	private String email = "";
	private String password = "";

	private String xpathToComposeButton = "//div[text() = 'COMPOSE' and @role = 'button']";
	private String xpathToComposeDialog = "//div[@class = 'dw']//div[@class = 'nH nn']//div[@class = 'nH Hd' and @role = 'dialog']";
	private String xpathToRecipientsField = xpathToComposeDialog + "//textarea[@name = 'to']";
	private String xpathToRecipientsAutocomplete = xpathToComposeDialog
			+ "//textarea[@name = 'to' and @aria-haspopup = 'true' and @aria-expanded = 'true']";
	private String xpathToSubjectField = "//input[@name = 'subjectbox']";
	private String xpathToMessageBody = "//div[@aria-label='Message Body']";
	private String xpathToSendButton = "//div[@role = 'button' and text() = 'Send']";
	private String xpathToTryNewEmailPopup = "//body/div/div/div[text() = 'Try the new Gmail']";
	private String xpathToTryNewEmailPopupCloseButton = "//body/div/div/div/button[@aria-label = 'Close']";
	private String xpathToErrorPopup = "//div[@role = 'alertdialog']";
	private String xpathToErrorPopupHeader = xpathToErrorPopup + "/div/span[text() = 'Error' and @role = 'heading']";

	public MailSender(String pathToChromeDriverExecutableFile) {
		this.pathToChromeDriverExecutableFile = pathToChromeDriverExecutableFile;
		initDriver(pathToChromeDriverExecutableFile);
	}

	public void initDriver(String pathToChromeDriverExecutableFile) {
		System.setProperty("webdriver.chrome.driver", pathToChromeDriverExecutableFile);
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--disable-browser-side-navigation");
		options.addArguments("--disable-gpu");
		driver = new ChromeDriver(options);
		wait = new WebDriverWait(driver, 10);
	}

	public void fillEmail(String emailAddress, String recipientName, String subject, String content)
			throws InterruptedException, Exception {
		for (WebElement errorPopup : driver.findElements(By.xpath(xpathToErrorPopupHeader))) {
			throw new Exception("\n=======\nThere is an error!\n" + driver.findElement(By.xpath(xpathToErrorPopup)).getText() + "\n===========\n");
		}
		String editedContent = "Hi " + recipientName.trim() + ",<br><br>" + content;
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpathToComposeButton)));
		driver.findElement(By.xpath(xpathToComposeButton)).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpathToComposeDialog)));
		driver.findElement(By.xpath(xpathToRecipientsField)).sendKeys(emailAddress);
		for (WebElement autoCompleteElement : driver.findElements(By.xpath(xpathToRecipientsAutocomplete))) {
			driver.findElement(By.xpath(xpathToRecipientsField)).sendKeys(Keys.ENTER);
		}
		try {
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpathToSubjectField)));
			driver.findElement(By.xpath(xpathToSubjectField)).click();
		} catch (Exception ex) {
			System.out.println("----------\nError: \n" + ex.getMessage() + "\n" + ex.getStackTrace() + "\n--\n");
			Logger.getLogger(MailSender.class.getName()).log(Level.SEVERE, null, ex);
			System.out.println("\n----------\n\nFalling back 1 step \n........\n");
			driver.findElement(By.xpath(xpathToRecipientsField)).sendKeys(Keys.ENTER);
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpathToSubjectField)));
			driver.findElement(By.xpath(xpathToSubjectField)).click();
			System.out.println("\n---------------");
		}
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpathToSubjectField)));
		driver.findElement(By.xpath(xpathToSubjectField)).sendKeys(subject);
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpathToMessageBody)));
		driver.findElement(By.xpath(xpathToMessageBody)).click();

		jsExecutor(injectEmailContentScript(editedContent));
	}

	public void loginEmail(String email, String password) {
		this.email = email;
		this.password = password;

		driver.navigate().to("https://mail.google.com/");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input [@type='email']")));
		driver.findElement(By.xpath("//input [@type='email']")).sendKeys(email);
		driver.findElement(By.xpath("//*[(self::div or self::input) and (@id='identifierNext' or @id = 'next')]"))
				.click();
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input [@type = 'password']")));
		driver.findElement(By.xpath("//input [@type = 'password']")).sendKeys(password);
		driver.findElement(By.xpath("//div[@id='passwordNext']")).click();

		for (WebElement tryNewPopup : driver.findElements(By.xpath(xpathToTryNewEmailPopup))) {
			System.out.println("Try New Popup found!!");
			driver.findElement(By.xpath(xpathToTryNewEmailPopupCloseButton)).click();
			;
		}
	}

	public void sendMail() {
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpathToSendButton)));
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
		String escapedContent = content.replace("'", "&#39;");
		
		return "function FindByAttributeValue(attribute, value, element_type)    {"
				+ "  element_type = element_type || \"*\";" + "  var All = document.getElementsByTagName(element_type);"
				+ "  for (var i = 0; i < All.length; i++)       {"
				+ "    if (All[i].getAttribute(attribute) == value) { return All[i]; }" + "  }" + "}"
				+ "FindByAttributeValue('aria-label','Message Body','div').innerHTML = '" + escapedContent + "'";
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
			Logger.getLogger(MailSender.class.getName()).log(Level.SEVERE, null, e);
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
				Logger.getLogger(MailSender.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

	}

	public String captureScreen() {
		String path = "";
		try {
			File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			path = System.getProperty("user.dir") + "/screenshots/" + source.getName();
			FileUtils.copyFile(source, new File(path));
			FileUtils.forceDelete(source);
		} catch (IOException e) {
			System.out.println("\n===========\nFailed to capture screenshot \n " + e.getMessage() + "\n"
					+ e.getStackTrace() + "\n===============");
			Logger.getLogger(MailSender.class.getName()).log(Level.SEVERE, null, e);
		}
		return path;
	}

	public void resetBrowser() {
		driver.quit();
		initDriver(pathToChromeDriverExecutableFile);
		loginEmail(email, password);
	}
}

import java.io.FileNotFoundException;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import entities.Recipient;
import exceltool.WorkingWithExcel;
import sender.MailSender;
import utils.FileUtilities;

public class Sender {
	public static void main(String[] args) throws InterruptedException, FileNotFoundException {

		MailSender sender = null;
		String targetEmail = "";
		HashMap<String, String> sendersInfo = new HashMap<String, String>();
		sendersInfo.put("", "");

		String recipientListFileName = "SendColdEmailTest3.xlsx";
		String sheetName = "SendColdEmail";

		int range = 100;
		int interval = 30000;
		int delay = 86400000;
		int lastRecipientIndex = 0;
		boolean isRecipientRemained = true;

		String logFileName = recipientListFileName.replace(".xlsx", "") + "-" + sheetName + ".txt";
		String configFileName = "tracker.txt";
		FileUtilities fUtils = new FileUtilities();

		String subject = "This is test email";
		String content = "I trust all is well. I&#39;m reaching out from TradaTesting. We are a leading services provider for QA &#38; testing for website, app, game and other softwares. <br><br>"
				+ "Our team have the ability to perform <b>full range testing</b> on all major mobile devices, web browsers and OSs, including iOS, Android, Windows, Mac, and Linux.<br><br>"
				+ "Our team is <b>well trained by Silicon Valley testing experts and all are good in English communicating.</b> "
				+ "With these advantages, we will be sure to provide <b>a top quality testing service</b> with the most <b>competitive cost (from 4.99 US Dollar/hour/tester).</b>"
				+ " We have been working with many well-known clients all over the world.<br><br>"
				+ "With <b>100+ most popular devices</b> in-house, we&#39;re positive to meet any testing demands.<br><br>"
				+ "To build confidence, we&#39;d be happy to offer you a <b>free trial test phase</b> so you can assess our quality.<br><br>"
				+ "If you have any questions or would like an in-depth conversation, we're available at: " + targetEmail
				+ "<br><br>" + "Best Regards,<br>" + "-Tra"
				+ "<br><br><div class=\"gmail_signature\" data-smartmail=\"gmail_signature\"><div dir=\"ltr\"><div style=\"font-size:12.8px\"><b style=\"font-size:12.8px\">QA Team</b><br></div>"
				+ "<div style=\"font-size:12.8px\"><b><font color=\"#134f5c\">TradaTesting</font></b></div>"
				+ "<div style=\"font-size:12.8px\"><img src=\"https://ci5.googleusercontent.com/proxy/FpGNXk1cd2jMN0_cHerewfY4DnBmdJ4V5U8Jkzh_wwznZQrEl7MV6zIlAKanOSXxiNLEcFLupOLHZEavdWuxzSNtc8YeN1U6sqGQM3L1UDBUvuVSiziRaTGI0G1V4GfMwL0qq4Kjzh5CUy27t6v1XSRnSxxscw75sf3UcQs53Eo7aHHeJv7R_4nBR-liK0CSqnADqexBUpfH4no2hsATQw=s0-d-e1-ft#https://docs.google.com/uc?export=download&amp;id=1Ove2h1rJEjNR3Mtrq4G6PgI65FcRl7UH&amp;revid=0B6g_h3xcq2pTTk5udGZWUTVYRDFuY2tIbnVnejVReDRNTTlRPQ\"><br></div>"
				+ "<div style=\"font-size:12.8px\"><p dir=\"ltr\" style=\"font-family:Arial,Helvetica,sans-serif;font-size:12.8px;line-height:1.38;margin-top:0pt;margin-bottom:0pt\"><span style=\"font-size:10pt;font-family:Arial;color:rgb(68,68,68);font-weight:700;vertical-align:baseline;white-space:pre-wrap\">M:</span>"
				+ "<span style=\"font-size:10pt;font-family:Arial;color:rgb(0,0,0);vertical-align:baseline;white-space:pre-wrap\"> (</span>"
				+ "<span style=\"font-size:10pt;font-family:Arial;vertical-align:baseline;white-space:pre-wrap\">+84) 963 455 335</span></p><p dir=\"ltr\" style=\"font-family:Arial,Helvetica,sans-serif;font-size:12.8px;line-height:1.38;margin-top:0pt;margin-bottom:0pt\">"
				+ "<span style=\"font-size:10pt;font-family:Arial;color:rgb(68,68,68);font-weight:700;vertical-align:baseline;white-space:pre-wrap\">W:</span><span style=\"font-size:10pt;font-family:Arial;color:rgb(0,0,0);vertical-align:baseline;white-space:pre-wrap\"> </span>"
				+ "<span style=\"font-size:10pt;font-family:Arial;vertical-align:baseline;white-space:pre-wrap\"><a href=\"http://www.tradatesting.com/\" style=\"color:rgb(17,85,204)\" target=\"_blank\">http://www.tradatesting.com</a></span></p><p dir=\"ltr\" style=\"font-family:Arial,Helvetica,sans-serif;font-size:12.8px;line-height:1.38;margin-top:0pt;margin-bottom:0pt\">"
				+ "<span style=\"font-size:10pt;font-family:Arial;color:rgb(68,68,68);font-weight:700;vertical-align:baseline;white-space:pre-wrap\">A:</span><span style=\"font-size:10pt;font-family:Arial;color:rgb(0,0,0);vertical-align:baseline;white-space:pre-wrap\"> 175 Khuong Thuong, Dong Da, Hanoi, Viet Nam"
				+ "</span></p><p dir=\"ltr\" style=\"font-family:Arial,Helvetica,sans-serif;font-size:12.8px;line-height:1.2;margin-top:0pt;margin-bottom:0pt;text-align:justify\"><span style=\"font-size:8pt;font-family:Arial;vertical-align:baseline;white-space:pre-wrap\">"
				+ "The information contained in this electronic mail message (including attachments) is privileged and confidential information intended solely for the use of Individual or Entity named above. If the reader of this message is not the intended recipient, you are hereby notified that you have received this message in error and that any retention, review, use, dissemination or copying of this communication or the information it contains is strictly prohibited. "
				+ "If you have received this communication in error, please immediately notify the sender by return e-mail, and delete the original message and all copies from your system. Thank you.</span></p></div></div></div></div>";

		String pathToChromeDriverExecutableFile = "./drivers/chromedriver";

		if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
			pathToChromeDriverExecutableFile = "./drivers/chromedriver.exe";
		}

		WorkingWithExcel excel = new WorkingWithExcel(recipientListFileName, sheetName);

		while (isRecipientRemained) {
			try {
				for (String senderEmail : sendersInfo.keySet()) {
					sender = new MailSender(pathToChromeDriverExecutableFile);
					String senderPassword = sendersInfo.get(senderEmail);
					sender.loginEmail(senderEmail, senderPassword);
					int rangeFrom = Integer.parseInt(fUtils.readFromFile("data/" + configFileName).trim());
					int rangeTo = rangeFrom + range;

					int counter = 0;
					ArrayList<Recipient> recipients = excel.getRecipientInfo(rangeFrom, rangeTo);
					if (recipients.size() < 1) {
						isRecipientRemained = false;
						throw new Exception("Empty recipients list");
					}
					for (Recipient recipient : recipients) {

						try {
							sender.fillEmail(recipient.getEmailAddress().trim(), recipient.getRecipientName().trim(),
									subject.trim(), content.trim());
						} catch (Exception e) {
							// TODO: handle exception
							sender.captureScreen();
							if (e.getMessage().contains("reached a limit")) {
								throw e;
							}
							System.out.println(
									"==========\n" + e.getMessage() + "\n" + e.getStackTrace() + "\n==========");
							Logger.getLogger(MailSender.class.getName()).log(Level.SEVERE, null, e);
							sender.resetBrowser();
							System.out.println("Retrying......");
							sender.fillEmail(recipient.getEmailAddress().trim(), recipient.getRecipientName().trim(),
									subject.trim(), content.trim());
						}
						sender.sendMail();

						String logContent = ", FirstName: " + recipient.getRecipientName().trim() + ", Email: "
								+ recipient.getEmailAddress().trim() + ", Sender: " + senderEmail;
						sender.writeLogSentEmail(logContent, logFileName);

						counter++;
						lastRecipientIndex++;
						System.out.println(counter + ". Sent to: " + recipient.getEmailAddress().trim() + "  - by : " + senderEmail);
						Thread.sleep(interval);
					}
					fUtils.writeToFile("data/" + configFileName, lastRecipientIndex + "", false);
					sender.quitDriver();
				}
				System.out.println("======= All done =======");
				Thread.sleep(delay);
			} catch (Exception e) {
				// TODO: handle exception
				fUtils.writeToFile("data/" + configFileName, lastRecipientIndex + "", false);
				System.out.println(e.getMessage() + "\n" + e.getStackTrace());
				Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, e);
			}
		}
		sender.quitDriver();
	}
}

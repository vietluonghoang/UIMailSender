package entities;

public class Recipient {
	private String emailAddress;
	private String recipientName;

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getRecipientName() {
		return recipientName;
	}

	public void setRecipientName(String recipientName) {
		this.recipientName = recipientName;
	}

	public Recipient(String emailAddress, String recipientName) {
		super();
		this.emailAddress = emailAddress;
		this.recipientName = recipientName;
	}
}

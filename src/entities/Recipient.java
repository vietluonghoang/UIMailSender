package entities;

public class Recipient {
	private String emailAddress;
	private String recipientName;
	private int index;

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

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

	public Recipient(int index, String emailAddress, String recipientName) {
		super();
		this.index = index;
		this.emailAddress = emailAddress;
		this.recipientName = recipientName;
	}
}

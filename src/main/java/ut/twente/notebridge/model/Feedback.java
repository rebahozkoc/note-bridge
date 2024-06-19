package ut.twente.notebridge.model;

public class Feedback extends BaseEntity {
	private String email;
	private String message;

	public Feedback() {
		super();
		this.email = null;
		this.message = null;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}

package ut.twente.notebridge.model;

public class Sponsor extends BaseUser {
	private String companyName;
	private String websiteURL;

	public Sponsor() {
		super();
		this.companyName = null;
		this.websiteURL = null;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getWebsiteURL() {
		return websiteURL;
	}

	public void setWebsiteURL(String websiteURL) {
		this.websiteURL = websiteURL;
	}
}

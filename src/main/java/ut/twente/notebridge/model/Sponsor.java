package ut.twente.notebridge.model;

/**
 * This class represents a sponsor in the application.
 * This class is used to store the information of a sponsor
 */
public class Sponsor extends BaseUser {
    /**
     * The name of the company.
     */
    private String companyName;
    /**
     * The website URL of the company.
     */
    private String websiteURL;

    /**
     * Default constructor for Sponsor.
     */
    public Sponsor() {
        super();
        this.companyName = null;
        this.websiteURL = null;
    }

    /**
     * getCompanyName method returns the name of the company.
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * setCompanyName method sets the name of the company.
     *
     * @param companyName The name of the company
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    /**
     * getWebsiteURL method returns the website URL of the company.
     */
    public String getWebsiteURL() {
        return websiteURL;
    }

    /**
     * setWebsiteURL method sets the website URL of the company.
     *
     * @param websiteURL The website URL of the company
     */
    public void setWebsiteURL(String websiteURL) {
        this.websiteURL = websiteURL;
    }
}

package ut.twente.notebridge.model;

import java.sql.Timestamp;

public class Post extends BaseEntity {

	private int personId;
	private String title;
	private String description;
	private int sponsoredBy;
	private Timestamp sponsoredFrom;
	private Timestamp sponsoredUntil;
	private String eventType;
	private String location;

	public Post() {
		super();
		this.personId = 0;
		this.title = null;
		this.description = null;
		this.sponsoredBy = 0;
		this.sponsoredFrom = null;
		this.sponsoredUntil = null;
		this.eventType = null;
		this.location = null;
	}

	public int getPersonId() {
		return personId;
	}

	public void setPersonId(int personId) {
		this.personId = personId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getSponsoredBy() {
		return sponsoredBy;
	}

	public void setSponsoredBy(int sponsoredBy) {
		this.sponsoredBy = sponsoredBy;
	}

	public Timestamp getSponsoredFrom() {
		return sponsoredFrom;
	}

	public void setSponsoredFrom(Timestamp sponsoredFrom) {
		this.sponsoredFrom = sponsoredFrom;
	}

	public Timestamp getSponsoredUntil() {
		return sponsoredUntil;
	}

	public void setSponsoredUntil(Timestamp sponsoredUntil) {
		this.sponsoredUntil = sponsoredUntil;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}

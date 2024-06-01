package ut.twente.notebridge.model;

import java.util.Date;
import java.util.List;

public class Post extends BaseEntity {

	//owner needs to be added
	private String title;
	private String description;
	private int likeCount;
	private int commentCount;
	private String sponsoredBy; // Optional
	private List<String> pictures; // Assuming pictures are stored as URLs or file paths
	private String eventType;
	private String location;
	private String message;

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

	public int getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public String getSponsoredBy() {
		return sponsoredBy;
	}

	public void setSponsoredBy(String sponsoredBy) {
		this.sponsoredBy = sponsoredBy;
	}

	public List<String> getPictures() {
		return pictures;
	}

	public void setPictures(List<String> pictures) {
		this.pictures = pictures;
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Post() {
		super();
		title=null;
		description=null;
		likeCount=0;
		commentCount=0;
		sponsoredBy=null;
		pictures=null;
		eventType=null;
		location=null;
		message=null;

	}
}

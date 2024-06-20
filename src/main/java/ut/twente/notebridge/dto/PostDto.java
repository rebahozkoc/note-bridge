package ut.twente.notebridge.dto;

import ut.twente.notebridge.model.Post;


public class PostDto extends Post {

	private boolean hasImage;
	private String image;

	public PostDto() {
		super();
		this.hasImage = false;
		this.image = null;
	}

	public void setHasImage(boolean hasImage) {
		this.hasImage = hasImage;
	}

	public boolean getHasImage() {
		return hasImage;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
		if (image != null) {
			this.hasImage = true;
		}
	}

	public void setPost(Post post) {
		this.setId(post.getId());
		this.setPersonId(post.getPersonId());
		this.setTitle(post.getTitle());
		this.setDescription(post.getDescription());
		this.setSponsoredBy(post.getSponsoredBy());
		this.setSponsoredFrom(post.getSponsoredFrom());
		this.setSponsoredUntil(post.getSponsoredUntil());
		this.setEventType(post.getEventType());
		this.setLocation(post.getLocation());
	}
}

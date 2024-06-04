package ut.twente.notebridge.model;

public class Picture {
	private String pictureURL;
	private int postId;

	public Picture() {
		super();
		this.pictureURL = null;
		this.postId = 0;
	}

	public String getPictureURL() {
		return pictureURL;
	}

	public void setPictureURL(String pictureURL) {
		this.pictureURL = pictureURL;
	}

	public int getPostId() {
		return postId;
	}

	public void setPostId(int postId) {
		this.postId = postId;
	}
}

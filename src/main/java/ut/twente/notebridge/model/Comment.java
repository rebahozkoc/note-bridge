package ut.twente.notebridge.model;

public class Comment extends BaseEntity{
	private String content;
	private int personId;
	private int postId;

	public Comment() {
		super();
		this.content = null;
		this.personId = 0;
		this.postId = 0;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getPersonId() {
		return personId;
	}

	public void setPersonId(int personId) {
		this.personId = personId;
	}

	public int getPostId() {
		return postId;
	}

	public void setPostId(int postId) {
		this.postId = postId;
	}
}

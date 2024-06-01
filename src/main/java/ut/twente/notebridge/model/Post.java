package ut.twente.notebridge.model;

public class Post extends BaseEntity {

	private String title;


	public Post() {
		super();
		title = null;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}

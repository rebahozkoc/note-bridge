package ut.twente.notebridge.model;

public class Person extends BaseUser {

	private String name;
	private String lastname;

	Person(){
		super();
		this.name = null;
		this.lastname = null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public void setBaseUser(BaseUser baseUser) {
		this.setId(baseUser.getId());
		this.setCreateDate(baseUser.getCreateDate());
		this.setLastUpdate(baseUser.getLastUpdate());
		this.setUsername(baseUser.getUsername());
		this.setPicture(baseUser.getPicture());
		this.setPhoneNumber(baseUser.getPhoneNumber());
		this.setPassword(baseUser.getPassword());
		this.setEmail(baseUser.getEmail());
	}
}

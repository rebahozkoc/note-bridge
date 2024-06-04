package ut.twente.notebridge.model;

import java.sql.Timestamp;

public class BaseEntity {
	private int id;
	private Timestamp createDate;
	private Timestamp lastUpdate;

	public BaseEntity() {
		super();
		this.id = 0;
		this.createDate = null;
		this.lastUpdate = null;
	}

	public boolean isValid() {
		return id != 0;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Timestamp getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}

	public Timestamp getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Timestamp lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
}

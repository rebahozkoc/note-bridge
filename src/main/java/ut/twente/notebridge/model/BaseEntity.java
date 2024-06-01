package ut.twente.notebridge.model;

public class BaseEntity {

    private String id;
    private String createDate;
    private String lastUpDate;

    public BaseEntity() {
        id = null;
        createDate = null;
        lastUpDate = null;
    }

    public boolean isValid() {
        return id != null && !id.isEmpty();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getLastUpDate() {
        return lastUpDate;
    }

    public void setLastUpDate(String lastUpDate) {
        this.lastUpDate = lastUpDate;
    }
}

package ut.twente.notebridge.model;

import java.sql.Timestamp;

/**
 * This class represents a base entity in the application.
 * Since id, createDate, lastUpdate are common fields in all entities, they are defined here
 * Other entity classes extend this class
 */
public class BaseEntity {
    /**
     * The user's unique ID.
     */
    private int id;
    /**
     * The date and time the user was created.
     */
    private Timestamp createDate;
    private Timestamp lastUpdate;

    /**
     * Default constructor for BaseEntity.
     */
    public BaseEntity() {
        super();
        this.id = 0;
        this.createDate = null;
        this.lastUpdate = null;
    }

    /**
     * getId method returns the id of the BaseEntity.
     */
    public int getId() {
        return id;
    }

    /**
     * setId method sets the id of the BaseEntity.
     *
     * @param id The id of the BaseEntity
     */


    public void setId(int id) {
        this.id = id;
    }

    /**
     * getCreateDate method returns the createDate of the BaseEntity.
     */

    public Timestamp getCreateDate() {
        return createDate;
    }

    /**
     * setCreateDate method sets the createDate of the BaseEntity.
     *
     * @param createDate The createDate of the BaseEntity
     */

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    /**
     * getLastUpdate method returns the lastUpdate of the BaseEntity.
     */

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    /**
     * setLastUpdate method sets the lastUpdate of the BaseEntity.
     *
     * @param lastUpdate The lastUpdate of the BaseEntity
     */
    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}

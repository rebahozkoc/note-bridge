package ut.twente.notebridge.model;

import java.util.HashMap;

/**
 * This class represents a collection of resources in the application.
 * This class is adopted from the provided examples by UT Twente.
 * This class is used for pagination of resources.
 */
public class ResourceCollection<T extends BaseEntity> {
    /**
     * The meta data of the collection.
     */
    public HashMap<String, Object> meta;
    /**
     * The data of the collection.
     */
    public T[] data;

    /**
     * Default constructor for ResourceCollection.
     */
    public ResourceCollection() {
        data = null;
        meta = new HashMap<>();
    }

    /**
     * Constructor for ResourceCollection.
     *
     * @param resources  The resources in the collection
     * @param pageSize   The size of the page
     * @param pageNumber The number of the page
     * @param total      The total number of resources
     */
    public ResourceCollection(T[] resources, int pageSize, int pageNumber, int total) {
        meta = new HashMap<>();

        meta.put("total", total);
        meta.put("pageNumber", pageNumber);
        meta.put("pageSize", pageSize);

        data = resources;
    }

}


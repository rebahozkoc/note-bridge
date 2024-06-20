package ut.twente.notebridge.model;

import java.util.HashMap;

public class ResourceCollection<T extends BaseEntity> {

	public HashMap<String,Object> meta;
	public T[] data;

	public ResourceCollection() {
		data = null;
		meta = new HashMap<>();
	}

	public ResourceCollection(T[] resources, int pageSize, int pageNumber, int total) {
		meta = new HashMap<>();

		meta.put("total",  total);
		meta.put("pageNumber", pageNumber);
		meta.put("pageSize", pageSize);

		data = resources;
	}

}


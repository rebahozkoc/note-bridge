package ut.twente.notebridge.model;

import java.util.HashMap;

public class ResourceCollection<T extends BaseEntity> {

	public HashMap<String,Object> meta;
	public HashMap<String,Object> links;
	public T[] data;

	public ResourceCollection() {
		data = null;
		meta = new HashMap<>();
		links = new HashMap<>();
	}

	public ResourceCollection(T[] resources, int pageSize, int pageNumber, int total) {
		meta = new HashMap<>();
		links = new HashMap<>();

		meta.put("total",  total);
		meta.put("pageNumber", pageNumber);
		meta.put("pageSize", pageSize);

		data = resources;
	}

}


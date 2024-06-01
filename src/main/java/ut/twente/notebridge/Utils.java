package ut.twente.notebridge;

import java.util.List;
import java.util.Objects;

public class Utils {

	public static String getAbsolutePathToResources() {
		return Objects.requireNonNull(Utils.class.getClassLoader().getResource("")).getPath();
	}

	public static <T extends Comparable<T>> int compare(T o1, T o2) {
		if (o1 == null && o2 == null)
			return 0;
		if (o1 == null)
			return -1;
		if (o2 == null)
			return 1;
		return o1.compareTo(o2);
	}

	public static List<?> pageSlice(List<?> list, int pageSize, int pageNumber) {
		int total = list.size();
		int firstIndex = (pageNumber - 1) * pageSize;
		int lastIndex = Math.min(pageNumber * pageSize, total);
		return list.subList(firstIndex,lastIndex);
	}

}
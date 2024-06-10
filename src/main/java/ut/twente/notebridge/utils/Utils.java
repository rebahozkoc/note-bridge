package ut.twente.notebridge.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
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

	public static String resultSetToJson(ResultSet resultSet) throws SQLException, JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		int columnCount = resultSet.getMetaData().getColumnCount();

		// Create a JSON object for the current row
		StringBuilder jsonBuilder = new StringBuilder("{");
		for (int i = 1; i <= columnCount; i++) {
			String columnName = resultSet.getMetaData().getColumnName(i);
			Object columnValue = resultSet.getObject(i);
			String jsonValue = objectMapper.writeValueAsString(columnValue);

			jsonBuilder.append("\"").append(columnName).append("\":").append(jsonValue);
			if (i < columnCount) {
				jsonBuilder.append(",");
			}
		}
		jsonBuilder.append("}");

		return jsonBuilder.toString();
	}

	public static Boolean checkPassword(String password) {
		if (password.length() < 8 || password.length() > 64) {
			return false;
		}

		boolean hasUppercase = false;
		int digitCount = 0;
		boolean hasSpecialChar = false;
		String specialChars = "!@#$%^&*()-_+=";

		for (char c : password.toCharArray()) {
			if (Character.isUpperCase(c)) {
				hasUppercase = true;
			} else if (Character.isDigit(c)) {
				digitCount++;
			} else if (specialChars.indexOf(c) >= 0) {
				hasSpecialChar = true;
			}
		}

		return hasUppercase && digitCount >= 2 && hasSpecialChar;
	}
}
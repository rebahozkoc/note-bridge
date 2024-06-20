package ut.twente.notebridge.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public abstract class Utils {


	public static String getAbsolutePathToResources() {
		return Objects.requireNonNull(Utils.class.getClassLoader().getResource("")).getPath();
	}

	public static <T extends Comparable<T>> int compare(T o1, T o2) {
		if (o1 == null && o2 == null) return 0;
		if (o1 == null) return -1;
		if (o2 == null) return 1;
		return o1.compareTo(o2);
	}

	public static List<?> pageSlice(List<?> list, int pageSize, int pageNumber) {
		int total = list.size();
		int firstIndex = (pageNumber - 1) * pageSize;
		int lastIndex = Math.min(pageNumber * pageSize, total);
		return list.subList(firstIndex, lastIndex);
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

	public static void saveToFile(InputStream uploadedInputStream, String uploadedFileLocation) {
		try {
			OutputStream out;
			int read = 0;
			byte[] bytes = new byte[1024];

			out = new FileOutputStream(uploadedFileLocation);
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			System.out.println("Error while saving file");
			e.printStackTrace();
		}
	}

	public static String readFromProperties(String key) {
		String rootPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath();
		String appConfigPath = rootPath + "app.properties";
		Properties appProps = new Properties();
		try {
			appProps.load(new FileInputStream(appConfigPath));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return appProps.getProperty(key);
	}
}
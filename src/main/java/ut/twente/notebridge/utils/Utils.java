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

/**
 * This class is used to handle not context-specific utility functions.
 */
public abstract class Utils {

    /**
     * This method is used to slice a list into pages.
     *
     * @param list       The list to be sliced
     * @param pageSize   The size of the page
     * @param pageNumber The number of the page
     * @return The sliced list
     */

    public static List<?> pageSlice(List<?> list, int pageSize, int pageNumber) {
        int total = list.size();
        int firstIndex = (pageNumber - 1) * pageSize;
        int lastIndex = Math.min(pageNumber * pageSize, total);
        return list.subList(firstIndex, lastIndex);
    }

    /**
     * This method is used to save a file with a given location.
     *
     * @param uploadedInputStream  The input stream of the file
     * @param uploadedFileLocation The location of the file
     */
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

    /**
     * This method is used to read the keys from the app.properties file
     *
     * @param key The key to be read
     * @return The value of the key
     */
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
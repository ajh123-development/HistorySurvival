package tk.minersonline.Minecart.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class FileUtils {

    private FileUtils() {}

    public static InputStream getFileFromResourceAsStream(String filePath) {
        InputStream inputStream = FileUtils.class.getClassLoader().getResourceAsStream(filePath);

        if (inputStream == null) {
            throw new IllegalArgumentException("Error while accessing resource, file not found! " + filePath);
        } else {
            return inputStream;
        }
    }

    public static String readFile(String filePath) throws IOException {
        InputStream stream = FileUtils.getFileFromResourceAsStream(filePath);
        int bufferSize = 1024;
        char[] buffer = new char[bufferSize];
        StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(stream, StandardCharsets.UTF_8);
        for (int numRead; (numRead = in.read(buffer, 0, buffer.length)) > 0; ) {
            out.append(buffer, 0, numRead);
        }
        return out.toString();
    }
}

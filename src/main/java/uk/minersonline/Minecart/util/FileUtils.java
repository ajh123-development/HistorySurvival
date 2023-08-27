package uk.minersonline.Minecart.util;

import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

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

    public static ByteBuffer readFromClasspath(final String name) {
        ByteBuffer buf;
        try (var channel = Channels.newChannel(
                Objects.requireNonNull(FileUtils.class.getClassLoader().getResourceAsStream(name), "The resource "+name+" cannot be found"))) {
            buf = BufferUtils.createByteBuffer(10);
            while (true) {
                var readbytes = channel.read(buf);
                if (readbytes == -1) break;
                if (buf.remaining() == 0) { // extend the buffer by 50%
                    var newBuf = BufferUtils.createByteBuffer(buf.capacity() * 3 / 2);
                    buf.flip();
                    newBuf.put(buf);
                    buf = newBuf;
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        buf.flip();
        return MemoryUtil.memSlice(buf); // we trim the final buffer to the size of the content
    }
}

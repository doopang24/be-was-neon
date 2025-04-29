package webserver.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.util.Constants;

import java.io.*;

public class StaticResourceLoader implements ResourceLoader {

    private static final Logger logger = LoggerFactory.getLogger(StaticResourceLoader.class);
    private final String PREFIX = "src/main/resources/static";

    @Override
    public FileResult fileToBytes(String requestURL) {
        File file = new File(PREFIX + requestURL);
        if(file.isDirectory()) {
            file = new File(file, Constants.DEFAULT_MAIN_PAGE);
            requestURL += Constants.DEFAULT_MAIN_PAGE;
        }

        try (FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[BUFFER_SIZE];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                out.write(buffer, OFFSET, read);
            }
            return new FileResult(out.toByteArray(), requestURL);
        } catch (IOException e) {
            logger.error("정적 파일을 읽는 중 오류 발생", e);
            throw new RuntimeException(e);
        }
    }
}

package webserver.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.util.Constants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class TemplateResourceLoader implements ResourceLoader {

    private static final Logger logger = LoggerFactory.getLogger(TemplateResourceLoader.class);
    private static final String PREFIX = "src/main/resources/templates";
    private static final String HTML = ".html";

    @Override
    public FileResult fileToBytes(String requestURL) {
        if (!requestURL.endsWith(".html")) requestURL += HTML;

        File file = new File(PREFIX, requestURL);
//        if(file.isDirectory()) {
//            file = new File(file, "list.html");
//            requestURL += Constants.DEFAULT_MAIN_PAGE;
//        }

        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[BUFFER_SIZE];
            int read;
            while ((read = fis.read(buffer)) != -1) {
                out.write(buffer, OFFSET, read);
            }
            return new FileResult(out.toByteArray(), requestURL);
        } catch (IOException e) {
            logger.error("템플릿 파일을 읽는 중 오류 발생", e);
            throw new RuntimeException(e);
        }
    }
}

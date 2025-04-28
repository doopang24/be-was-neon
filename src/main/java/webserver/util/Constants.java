package webserver.util;

public class Constants {

    private Constants() {}

    // HTTP 버전
    public static final String HTTP_VERSION = "HTTP/1.1";

    // request line 관련
    public static final String CREATION_PATH = "/create";
    public static final String GET = "GET";
    public static final String POST = "POST";

    // 헤더 관련
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String LOCATION = "Location";
    public static final String FORM_URL_ENCODED = "application/x-www-form-urlencoded";
    public static final String COOKIE = "cookie";
    public static final String SET_COOKIE = "Set-Cookie";
    public static final String JSESSIONID = "JSESSIONID=";
    public static final String PATH_ATTRIBUTE_SETTING = "; Path=/; HttpOnly";


    // MIME 관련
    public static final String PLAIN_TEXT = "text/plain";

    // User 관련
    public static final String USER_ID = "userId";
    public static final String USER_PASSWORD = "password";

    // 기타
    public static final String DEFAULT_MAIN_PAGE = "/index.html";
    public static final String ROOT_PATH = "/";

}

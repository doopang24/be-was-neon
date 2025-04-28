package webserver.handler;

import webserver.request.HttpRequest;
import webserver.request.RequestParser;
import webserver.response.HttpResponse;
import webserver.response.Status;
import webserver.session.SessionMap;
import webserver.util.Constants;

public class LogoutHandler implements Handler {

    private final String MAX_AGE_ATTRIBUTE = "; Max-Age=0";

    @Override
    public HttpResponse handle(HttpRequest request) {
        String cookie = request.getHeaders().get(Constants.COOKIE);
        RequestParser.extractSessionId(cookie).ifPresent(SessionMap::removeSession);
        return createResponse();
    }

    private HttpResponse createResponse() {
        return HttpResponse.getBuilder()
                .httpVersion(Constants.HTTP_VERSION)
                .status(Status.FOUND)
                .header(Constants.LOCATION, Constants.ROOT_PATH)
                .header(Constants.SET_COOKIE, Constants.JSESSIONID + MAX_AGE_ATTRIBUTE + Constants.PATH_ATTRIBUTE_SETTING)
                .build();
    }
}

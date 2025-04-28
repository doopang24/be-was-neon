package webserver.handler;

import db.Database;
import model.User;
import webserver.request.HttpRequest;
import webserver.request.RequestParser;
import webserver.response.HttpResponse;
import webserver.response.Status;
import webserver.session.Session;
import webserver.session.SessionMap;
import webserver.util.Constants;

import java.util.HashMap;
import java.util.Map;

public class LoginHandler implements Handler {

    private final String LOGIN_FAIL_PATH = "/login_failed";

    @Override
    public HttpResponse handle(HttpRequest request) {
        Map<String, String> bodyParams = new HashMap<>();
        if (request.getHeaders().get(Constants.CONTENT_TYPE.toLowerCase()).equals(Constants.FORM_URL_ENCODED)) {
            bodyParams = RequestParser.parseQueryString(request.getBody());
        }
        User user = findUser(bodyParams);
        // 찾아온 User 가 null 이거나, 비밀번호가 맞지 않으면 로그인 실패 페이지로 이동
        if (user == null || !isPasswordCorrect(user, bodyParams.get(Constants.USER_PASSWORD))) return createLoginFailedResponse();

        String sessionId = SessionMap.putSession(new Session(user));
        return createResponse(sessionId);
    }

    private User findUser(Map<String, String> param) {
        return Database.findUserById(param.get(Constants.USER_ID));
    }

    private boolean isPasswordCorrect(User user, String password) {
        return user.getPassword().equals(password);
    }

    private HttpResponse createResponse(String sessionId) {
        return HttpResponse.getBuilder()
                .httpVersion(Constants.HTTP_VERSION)
                .status(Status.FOUND)
                .header(Constants.LOCATION, Constants.ROOT_PATH)
                .header(Constants.SET_COOKIE, Constants.JSESSIONID + sessionId + Constants.PATH_ATTRIBUTE_SETTING)
                .build();
    }

    private HttpResponse createLoginFailedResponse() {
        return HttpResponse.getBuilder()
                .httpVersion(Constants.HTTP_VERSION)
                .status(Status.FOUND)
                .header(Constants.LOCATION, LOGIN_FAIL_PATH)
                .build();
    }
}

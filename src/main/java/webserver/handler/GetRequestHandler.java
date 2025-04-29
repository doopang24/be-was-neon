package webserver.handler;

import db.Database;
import model.User;
import webserver.loader.StaticResourceLoader;
import webserver.loader.FileResult;
import webserver.loader.ResourceLoader;
import webserver.loader.TemplateResourceLoader;
import webserver.request.HttpRequest;
import webserver.request.RequestParser;
import webserver.response.HttpResponse;
import webserver.response.Status;
import webserver.session.SessionMap;
import webserver.util.Constants;
import webserver.util.ContentType;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class GetRequestHandler implements Handler {

    private final String USER_LIST = "/user/list";
    private final String LOGIN_STATE_BUTTONS = "<!--LOGIN_STATE_BUTTONS-->";
    private final String LOGGED_IN_USER_NAME = "@{logged_in_User_name}";

    @Override
    public HttpResponse handle(HttpRequest request) {
        String path = request.getUrlPath();
        if (path.equals(Constants.ROOT_PATH)) path = Constants.DEFAULT_MAIN_PAGE;

        ResourceLoader loader;

        if (path.equals(Constants.DEFAULT_MAIN_PAGE) || path.equals(USER_LIST)) {
            loader = new TemplateResourceLoader();
            FileResult fileResult = loader.fileToBytes(path);
            return handleTemplates(request, fileResult);
        } else {
            loader = new StaticResourceLoader();
        }
        FileResult fileResult = loader.fileToBytes(path);
        return createResponse(fileResult);
    }

    private HttpResponse handleTemplates(HttpRequest request, FileResult fileResult) {
        String html = new String(fileResult.body(), StandardCharsets.UTF_8);
        byte[] body = replaceLoginRelatedPlaceHolders(request, html);
        return createResponse(new FileResult(body, fileResult.resolvedPath()));
    }

    private HttpResponse createResponse(FileResult fileResult) {
        return HttpResponse.getBuilder()
                .httpVersion(Constants.HTTP_VERSION)
                .status(Status.OK)
                .header(Constants.CONTENT_TYPE, ContentType.getContentType(fileResult.resolvedPath()))
                .header(Constants.CONTENT_LENGTH, String.valueOf(fileResult.body().length))
                .body(fileResult.body())
                .build();
    }

    private boolean isLoggedIn(HttpRequest request) {
        return extractSessionId(request)
                .flatMap((SessionMap::getSession))
                .isPresent();
    }

    private Optional<String> extractSessionId(HttpRequest request) {
        String cookie = request.getHeaders().get(Constants.COOKIE);
        if (cookie == null) return Optional.empty();
        return RequestParser.extractSessionId(cookie);
    }

    private String createLoginStateButtons(boolean isLoggedIn) {
        if (isLoggedIn) {
            return """
                    <li class="header__menu__item">
                        <form action="/logout" method="post">
                            <button class="btn btn_contained btn_size_s" type="submit">로그아웃</button>
                        </form>
                    </li>
                    <li class="header__menu__item">
                      <a class="btn btn_ghost btn_size_s" href="/user/list">사용자 목록</a>
                    </li>
                    """;
        } else {
            return """
                    <li class="header__menu__item">
                        <a class="btn btn_contained btn_size_s" href="/login">로그인</a>
                    </li>
                    <li class="header__menu__item">
                      <a class="btn btn_ghost btn_size_s" href="/registration">회원 가입</a>
                    </li>
                    """;
        }
    }

    private byte[] replaceLoginRelatedPlaceHolders(HttpRequest request, String html) {
        boolean isLoggedIn = isLoggedIn(request);
        String loginButtonHtml = createLoginStateButtons(isLoggedIn);

        html = html.replace(LOGIN_STATE_BUTTONS, loginButtonHtml);
        String userName = extractSessionId(request)
                .flatMap(SessionMap::getUserId)
                .map(Database::findUserById)
                .map(User::getName)
                .orElse("guest");

        html = html.replace(LOGGED_IN_USER_NAME, userName);

        String userListHtml = createUserListHtml();
        html = html.replace("<!--USER_LIST-->", userListHtml);

        return html.getBytes(StandardCharsets.UTF_8);
    }

    private String createUserListHtml() {
        StringBuilder builder = new StringBuilder();
        for(User user : Database.findAll()) {
            builder.append("<li class=\"user-list__item\">")
                    .append("<img class=\"user-list__img\" src=\"/img/default-profile.svg\" alt=\"User Image\" />")
                    .append("<div style=\"display: flex; flex-direction: column; align-items: flex-start; gap: 4px;\">")
                    .append("<span class=\"user-list__name\">").append(user.getUserId()).append("</span>")
                    .append("<span style=\"font-size: 16px; color: #555;\">").append(user.getName()).append("</span>")
                    .append("<span style=\"font-size: 14px; color: #777;\">").append(user.getEmail()).append("</span>")
                    .append("</div>")
                    .append("</li>");
        }
        return builder.toString();
    }
}

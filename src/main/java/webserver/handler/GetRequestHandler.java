package webserver.handler;

import db.Database;
import model.User;
import webserver.loader.FileResourceLoader;
import webserver.loader.FileResult;
import webserver.loader.ResourceLoader;
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

    private final String INDEX_HTML = "/index.html";

    @Override
    public HttpResponse handle(HttpRequest request) {
        String path = request.getUrlPath();
        if (path.equals(Constants.ROOT_PATH)) path = Constants.DEFAULT_MAIN_PAGE;

        ResourceLoader loader = new FileResourceLoader();
        FileResult fileResult = loader.fileToBytes(path);

        if (path.endsWith(INDEX_HTML)) {
            String html = new String(fileResult.body(), StandardCharsets.UTF_8);
            byte[] body = replaceLoginRelatedPlaceHolders(request, html);
            return createResponse(new FileResult(body, fileResult.resolvedPath()));
        }
        return createResponse(fileResult);
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
                      <a class="btn btn_contained btn_size_s" href="/logout">로그아웃</a>
                    </li>
                    <li class="header__menu__item">
                      <a class="btn btn_ghost btn_size_s" href="/users">사용자 목록</a>
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

        html = html.replace("<--LOGIN_STATE_BUTTONS-->", loginButtonHtml);
        String userName = extractSessionId(request)
                .flatMap(SessionMap::getUserId)
                .map(Database::findUserById)
                .map(User::getName)
                .orElse("guest");

        html = html.replace("@{name}", userName);
        return html.getBytes(StandardCharsets.UTF_8);
    }
}

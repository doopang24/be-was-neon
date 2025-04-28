package webserver.session;

import model.User;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionMap {
    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();

    private SessionMap() {}

    public static String putSession(Session session) {
        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, session);
        return sessionId;
    }

    public static Optional<Session> getSession(String sessionId) {
        return Optional.ofNullable(sessions.get(sessionId));
    }

    public static void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }

    public static Optional<String> getUserId(String sessionId) {
        return Optional.ofNullable(sessions.get(sessionId))
                .map(Session::getUser)
                .map(User::getUserId);
    }
}

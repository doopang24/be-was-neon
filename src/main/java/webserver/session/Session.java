package webserver.session;

import model.User;

public class Session {
    private final User user;
    private final long createdAt;

    public Session(User user) {
        this.user = user;
        this.createdAt = System.currentTimeMillis();
    }

    public User getUser() {
        return user;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public boolean isExpired(long timeoutMillis) {
        return System.currentTimeMillis() - createdAt > timeoutMillis;
    }
}

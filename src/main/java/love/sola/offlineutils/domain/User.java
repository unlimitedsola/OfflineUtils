package love.sola.offlineutils.domain;

import java.util.UUID;

public class User {

    private final String name;
    private final UUID uuid;
    private final String secret;

    public User(String name, UUID uuid, String secret) {
        this.name = name;
        this.uuid = uuid;
        this.secret = secret;
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getSecret() {
        return secret;
    }
}

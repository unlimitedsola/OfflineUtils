package love.sola.offlineutils;

import org.mindrot.BCrypt;

public class Crypto {

    public static boolean check(String raw, String secret) {
        return BCrypt.checkpw(raw, secret);
    }

    public static String hash(String raw) {
        return BCrypt.hashpw(raw, BCrypt.gensalt());
    }
}

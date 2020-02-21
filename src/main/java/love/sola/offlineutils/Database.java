package love.sola.offlineutils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import love.sola.offlineutils.domain.User;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Database {

    private static final Path DB_FILE_PATH = Paths.get("passwd.json");

    private static final Gson json = new Gson();
    private static Map<UUID, User> db;

    public static void load() throws IOException {
        if (!Files.exists(DB_FILE_PATH)) {
            db = new HashMap<>();
            return;
        }
        String content = new String(Files.readAllBytes(DB_FILE_PATH), StandardCharsets.UTF_8);
        Type type = new TypeToken<List<User>>() {}.getType();
        List<User> userList = json.fromJson(content, type);
        db = userList.stream().collect(Collectors.toMap(User::getUuid, Function.identity()));
    }

    public static void save() throws IOException {
        String content = json.toJson(db.values());
        Files.write(DB_FILE_PATH, content.getBytes(StandardCharsets.UTF_8));
    }

    public static User get(UUID uuid) {
        return db.get(uuid);
    }

    public static void put(User user) {
        db.put(user.getUuid(), user);
        try {
            save();
        } catch (IOException e) {
            OfflineUtils.logger.error("Error occurred while saving changes", e);
        }
    }
}

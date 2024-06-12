package taskmanager;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http.adapters.InstantAdapter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.time.Instant;

public class Managers {
    public static TaskManager getDefault() {

        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {

        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.STATIC, Modifier.FINAL)
                .create();
    }

    public static TaskManager getFileBackedTaskManager(final File file) throws IOException {
        return FileBackedTaskManager.loadFromFile(file);
    }
}

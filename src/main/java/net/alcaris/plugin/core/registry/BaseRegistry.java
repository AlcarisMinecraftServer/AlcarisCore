package net.alcaris.plugin.core.registry;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.alcaris.plugin.core.AlcarisCore;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseRegistry<T> {
    protected final Map<String, T> cache = new ConcurrentHashMap<>();
    protected final Gson gson = new Gson();
    protected final AlcarisCore plugin;

    public BaseRegistry(AlcarisCore plugin) {
        this.plugin = plugin;
    }

    public Optional<T> get(String id) {
        return Optional.ofNullable(cache.get(id));
    }

    public List<T> getAll() {
        return List.copyOf(cache.values());
    }

    public CompletableFuture<Void> reloadAsync() {
        return fetchFromApi().thenAccept(items -> {
            cache.clear();
            for (T item : items) {
                String key = getKey(item);
                cache.put(key, item);
                saveItemToFile(key, item);
            }
        });
    }

    public void loadAllFromDisk(File directory) {
        if (!directory.exists() || !directory.isDirectory()) return;

        File[] files = directory.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) return;

        for (File file : files) {
            try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
                T item = gson.fromJson(reader, getType());
                String key = getKey(item);
                cache.put(key, item);
            } catch (IOException e) {
                plugin.getLogger().warning("[AlcarisCore] Failed to load cache file: " + file.getName());
            }
        }
    }

    protected void saveItemToFile(String key, T item) {
        File dir = new File("plugins/AlcarisCore/caches/" + getCategory());
        if (!dir.exists()) dir.mkdirs();

        File file = new File(dir, key + ".json");
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            gson.toJson(item, writer); // 非整形で出力
        } catch (IOException e) {
            plugin.getLogger().warning("[AlcarisCore] Failed to save cache file: " + key);
        }
    }

    protected abstract CompletableFuture<List<T>> fetchFromApi();

    protected abstract String getKey(T item);

    protected abstract String getCategory();

    protected abstract Type getType();
}

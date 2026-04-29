package net.alcaris.plugin.core.registry;

import com.google.gson.Gson;
import net.alcaris.plugin.core.AlcarisCore;
import org.bukkit.Bukkit;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseRegistry<T> {
    protected volatile Map<String, T> cache = new ConcurrentHashMap<>();
    protected final Gson gson = new Gson();
    protected final AlcarisCore plugin;
    private final File cacheDirectory;

    public BaseRegistry(AlcarisCore plugin) {
        this.plugin = plugin;
        this.cacheDirectory = new File(plugin.getDataFolder(), "caches/" + getCategory());
        if (!cacheDirectory.exists()) {
            cacheDirectory.mkdirs();
        }
    }

    public Optional<T> get(String id) {
        return Optional.ofNullable(cache.get(id));
    }

    public List<T> getAll() {
        return List.copyOf(cache.values());
    }

    public int size() {
        return cache.size();
    }

    public CompletableFuture<Void> reloadAsync() {
        return fetchFromApi().thenAccept(items -> {
            Map<String, T> next = new ConcurrentHashMap<>();
            for (T item : items) {
                next.put(getKey(item), item);
            }
            this.cache = next;

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                for (T item : items) {
                    saveItemToFile(getKey(item), item);
                }
            });
        });
    }

    public void loadAllFromDisk() {
        if (!cacheDirectory.exists() || !cacheDirectory.isDirectory()) {
            plugin.getLogger().warning("Cache directory not found: " + cacheDirectory.getPath());
            return;
        }

        File[] files = cacheDirectory.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null || files.length == 0) {
            plugin.getLogger().info("No cached " + getCategory() + " files found");
            return;
        }

        int loaded = 0;
        for (File file : files) {
            try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
                T item = gson.fromJson(reader, getType());
                if (item != null) {
                    String key = getKey(item);
                    cache.put(key, item);
                    loaded++;
                }
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to load cache file: " + file.getName());
            }
        }

        plugin.getLogger().info("Loaded " + loaded + " " + getCategory() + "(s) from disk cache");
    }

    protected void saveItemToFile(String key, T item) {
        File file = new File(cacheDirectory, key + ".json");
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            gson.toJson(item, writer);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save cache file: " + key);
        }
    }

    protected abstract CompletableFuture<List<T>> fetchFromApi();
    protected abstract String getKey(T item);
    protected abstract String getCategory();
    protected abstract Type getType();
}

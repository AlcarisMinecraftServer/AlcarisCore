package net.alcaris.plugin.core.service;

import net.alcaris.plugin.core.AlcarisCore;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class DataSyncManager {
    private final Map<String, Supplier<CompletableFuture<Void>>> registryReloaders = new ConcurrentHashMap<>();
    private final AlcarisCore plugin;

    public DataSyncManager(AlcarisCore plugin) {
        this.plugin = plugin;
    }

    public void register(String category, Supplier<CompletableFuture<Void>> asyncReloader) {
        registryReloaders.put(category.toLowerCase(), asyncReloader);
        plugin.getLogger().info("Registered data sync handler: " + category);
    }

    public CompletableFuture<Void> reload(String category) {
        var reloader = registryReloaders.get(category.toLowerCase());
        if (reloader == null) {
            plugin.getLogger().warning("No reload handler registered for category: " + category);
            return CompletableFuture.completedFuture(null);
        }

        return reloader.get()
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        plugin.getLogger().severe("Failed to reload " + category + ": " + ex.getMessage());
                    } else {
                        plugin.getLogger().info("Reloaded: " + category);
                    }
                });
    }

    public void reloadSync(String category) {
        reload(category).join();
    }

    public CompletableFuture<Void> reloadAll() {
        plugin.getLogger().info("Reloading all registered categories...");

        CompletableFuture<?>[] futures = registryReloaders.entrySet().stream()
                .map(entry -> entry.getValue().get()
                        .whenComplete((result, ex) -> {
                            if (ex != null) {
                                plugin.getLogger().severe("Failed to reload " + entry.getKey() + ": " + ex.getMessage());
                            }
                        }))
                .toArray(CompletableFuture[]::new);

        return CompletableFuture.allOf(futures)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        plugin.getLogger().info("All categories reloaded successfully");
                    }
                });
    }
}

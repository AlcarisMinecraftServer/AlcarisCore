package net.alcaris.plugin.core.lib;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class DataSyncManager {

    private final Map<String, Supplier<CompletableFuture<Void>>> registryReloaders = new ConcurrentHashMap<>();

    public void register(String category, Supplier<CompletableFuture<Void>> asyncReloader) {
        registryReloaders.put(category.toLowerCase(), asyncReloader);
    }

    public CompletableFuture<Void> reload(String category) {
        var reloader = registryReloaders.get(category.toLowerCase());
        if (reloader == null) return CompletableFuture.completedFuture(null);
        return reloader.get();
    }

    public void reloadSync(String category) {
        reload(category).join();
    }

    public void reloadAll() {
        registryReloaders.values().forEach(Supplier::get);
    }
}

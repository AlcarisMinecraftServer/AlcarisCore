package net.alcaris.plugin.core.registry;

import net.alcaris.plugin.core.AlcarisCore;
import net.alcaris.plugin.core.api.ApiClient;
import net.alcaris.plugin.core.event.ItemRegistryReloadEvent;
import net.alcaris.plugin.core.model.item.ItemBaseModel;
import org.bukkit.Bukkit;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ItemRegistry extends BaseRegistry<ItemBaseModel> {

    private final ApiClient apiClient;

    public ItemRegistry(AlcarisCore plugin, ApiClient apiClient) {
        super(plugin);
        this.apiClient = apiClient;
    }

    @Override
    public CompletableFuture<Void> reloadAsync() {
        return super.reloadAsync().thenRun(() ->
            Bukkit.getScheduler().runTask(plugin, () ->
                Bukkit.getPluginManager().callEvent(new ItemRegistryReloadEvent())
            )
        );
    }

    @Override
    protected CompletableFuture<List<ItemBaseModel>> fetchFromApi() {
        return apiClient.fetchItemsAsync().thenApply(items -> {
            int created = 0;
            int updated = 0;
            int unchanged = 0;

            for (ItemBaseModel item : items) {
                String id = item.getId();
                ItemBaseModel old = cache.get(id);

                if (old == null) {
                    created++;
                } else if (item.getVersion() != old.getVersion()) {
                    updated++;
                } else {
                    unchanged++;
                }
            }

            if (created + updated > 0) {
                plugin.getLogger().info(String.format(
                        "Item sync: %d total (%d new, %d updated, %d unchanged)",
                        items.size(), created, updated, unchanged
                ));
            }

            return items;
        });
    }

    @Override
    protected String getKey(ItemBaseModel item) {
        return item.getId();
    }

    @Override
    protected String getCategory() {
        return "item";
    }

    @Override
    protected Type getType() {
        return ItemBaseModel.class;
    }
}

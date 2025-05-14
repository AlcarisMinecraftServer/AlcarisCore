package net.alcaris.plugin.core.registry;

import net.alcaris.plugin.core.AlcarisCore;
import net.alcaris.plugin.core.lib.ApiClient;
import net.alcaris.plugin.core.model.item.ItemBaseModel;

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
    protected CompletableFuture<List<ItemBaseModel>> fetchFromApi() {
        return apiClient.fetchItemsAsync().thenApply(items -> {
            Map<String, ItemBaseModel> newMap = new HashMap<>();

            for (ItemBaseModel item : items) {
                String id = item.getId();
                ItemBaseModel old = cache.get(id);

                if (item.getVersion() != old.getVersion()) {
                    plugin.getLogger().info("<item> Updated: " + id +
                            " (" + old.getVersion() + " → " + item.getVersion() + ")");
                }
                newMap.put(id, item);
            }

            cache.clear();
            cache.putAll(newMap);
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

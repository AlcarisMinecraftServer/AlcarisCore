package net.alcaris.plugin.core;

import net.alcaris.plugin.core.commands.CommandManager;
import net.alcaris.plugin.core.commands.SubCommand;
import net.alcaris.plugin.core.http.ApiClient;
import net.alcaris.plugin.core.lib.APIWebSocketClient;
import net.alcaris.plugin.core.lib.DataSyncManager;
import net.alcaris.plugin.core.registry.ItemRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URI;
import java.util.Objects;

public final class AlcarisCore extends JavaPlugin {
    public final MiniMessage miniMessage = MiniMessage.miniMessage();
    public Component prefix = miniMessage.deserialize("[<gradient:#8a80ff:#9400d9>AlcarisCore</gradient>] ");

    private CommandManager CommandManager;
    private ApiClient apiClient;
    private ItemRegistry itemRegistry;
    private DataSyncManager dataSyncManager;
    private APIWebSocketClient webSocketClient;

    @Override
    public void onEnable() {
        // Load Config
        saveDefaultConfig();
        FileConfiguration config = getConfig();

        // Command Manager
        CommandManager = new CommandManager();

        PluginCommand alcarisCommand = getCommand("alcaris");

        if (alcarisCommand == null) {
            getLogger().severe("Failed to register command. Server shutting down due to fatal error.");
            getServer().shutdown();
            return;
        }

        alcarisCommand.setExecutor(CommandManager);
        alcarisCommand.setTabCompleter(CommandManager);

        // API and Data Manager
        String apiUrl = config.getString("general.apiUrl");
        String apiKey = config.getString("general.apiKey");

        this.apiClient = new ApiClient(apiUrl, apiKey);
        this.itemRegistry = new ItemRegistry(this, apiClient);
        this.dataSyncManager = new DataSyncManager();
        this.dataSyncManager.register("item", () -> itemRegistry.reloadAsync());

        itemRegistry.reloadAsync()
                .thenRun(() -> getLogger().info("Initial loading of item data completed."))
                .exceptionally(ex -> {
                    getLogger().severe("Failed to load item data: " + ex.getMessage());
                    getServer().shutdown();
                    return null;
                });

        // Connect WebSocket
        try {
            String socketUrl = Objects.requireNonNull(apiUrl)
                    .replaceFirst("^https", "wss")
                    .replaceFirst("^http",  "ws") + "/ws";
            URI socketUri = URI.create(socketUrl);

            if (webSocketClient == null || !webSocketClient.isOpen()) {
                webSocketClient = new APIWebSocketClient(socketUri, apiKey, this);
                webSocketClient.connect();
                getLogger().info("WebSocket connected.");
            }
        } catch (Exception e) {
            getLogger().severe("Failed to connect to WebSocket: " + e.getMessage());
            getServer().shutdown();
        }
    }

    @Override
    public void onDisable() {
        if (webSocketClient != null) {
            try {
                webSocketClient.close();
                getLogger().info("WebSocket closed.");
            } catch (Exception e) {
                getLogger().warning("Couldn't close WebSocket cleanly: " + e.getMessage());
            } finally {
                webSocketClient = null;
            }
        }
    }

    public void registerSubCommand(String name, SubCommand command) {
        CommandManager.register(name, command);
    }

    public ItemRegistry getItemRegistry() {
        return itemRegistry;
    }

    public DataSyncManager getDataSyncManager() {
        return dataSyncManager;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }
}

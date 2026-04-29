package net.alcaris.plugin.core;

import net.alcaris.plugin.core.api.ApiClient;
import net.alcaris.plugin.core.api.websocket.APIWebSocketClient;
import net.alcaris.plugin.core.config.CoreConfig;
import net.alcaris.plugin.core.database.DatabaseManager;
import net.alcaris.plugin.core.service.DataSyncManager;
import net.alcaris.plugin.core.registry.ItemRegistry;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URI;
import java.sql.SQLException;

public final class AlcarisCore extends JavaPlugin {
    private CoreConfig config;
    private DatabaseManager databaseManager;
    private ApiClient apiClient;
    private ItemRegistry itemRegistry;
    private DataSyncManager dataSyncManager;
    private APIWebSocketClient webSocketClient;

    private volatile boolean initialized = false;

    @Override
    public void onEnable() {
        try {
            // Load Config
            saveDefaultConfig();
            this.config = new CoreConfig(getConfig());

            if (!this.config.validate()) {
                shutdownWithError("Invalid configuration. Check config.yml");
                return;
            }

            // Load Database
            try {
                this.databaseManager = new DatabaseManager(this, this.config);
                this.databaseManager.initialize();
                getLogger().info("Database connection established");
            } catch (SQLException e) {
                shutdownWithError("Failed to initialize database: " + e.getMessage());
                return;
            }

            // Load API Client
            this.apiClient = new ApiClient(
                    this.config.getApiUrl(),
                    this.config.getApiKey(),
                    this
            );

            this.itemRegistry = new ItemRegistry(this, apiClient);
            this.dataSyncManager = new DataSyncManager(this);
            this.dataSyncManager.register("item", () -> itemRegistry.reloadAsync());

            // Load itemRegistry form cache
            itemRegistry.loadAllFromDisk();
            getLogger().info("Loaded " + itemRegistry.size() + " items from cache");

            itemRegistry.reloadAsync()
                    .thenRun(() -> {
                        getLogger().info("Item data synchronized from API");
                        initialized = true;
                        Bukkit.getScheduler().runTaskAsynchronously(this, this::connectWebSocket);
                    })
                    .exceptionally(ex -> {
                        getLogger().severe("CRITICAL: Failed to sync item data from API");
                        getLogger().severe("Reason: " + ex.getMessage());
                        shutdownWithError("API synchronization failed");
                        return null;
                    });
        } catch (Exception e) {
            shutdownWithError("Fatal error during initialization: " + e.getMessage());
        }
    }

    private void connectWebSocket() {
        try {
            String socketUrl = this.config.getApiUrl()
                    .replaceFirst("^https", "wss")
                    .replaceFirst("^http", "ws") + "/ws";
            URI socketUri = URI.create(socketUrl);

            this.webSocketClient = new APIWebSocketClient(socketUri, this.config.getApiKey(), this);
            this.webSocketClient.connectBlocking();
        } catch (Exception e) {
            getLogger().severe("WebSocket connection failed: " + e.getMessage());
            getLogger().warning("Server will run without real-time updates");
        }
    }

    @Override
    public void onDisable() {
        if (webSocketClient != null) {
            try {
                webSocketClient.close();
                getLogger().info("WebSocket closed.");
            } catch (Exception e) {
                getLogger().warning("Failed to close WebSocket: " + e.getMessage());
            }
        }

        if (apiClient != null) {
            apiClient.shutdown();
            getLogger().info("API client shutdown");
        }

        if (databaseManager != null) {
            try {
                databaseManager.shutdown();
                getLogger().info("Database connection pool closed");
            } catch (Exception e) {
                getLogger().warning("Failed to close database: " + e.getMessage());
            }
        }
    }

    private void shutdownWithError(String reason) {
        getLogger().severe("========================================");
        getLogger().severe("FATAL ERROR: " + reason);
        getLogger().severe("Server will shut down in 3 seconds...");
        getLogger().severe("========================================");

        Bukkit.getScheduler().runTaskLater(this, () -> Bukkit.getServer().shutdown(), 60L);
    }

    // Getters
    public CoreConfig getCoreConfig() {
        return config;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
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

    public boolean isInitialized() {
        return initialized;
    }
}

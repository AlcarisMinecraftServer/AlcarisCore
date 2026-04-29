package net.alcaris.plugin.core.api.websocket;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.alcaris.plugin.core.AlcarisCore;
import net.alcaris.plugin.core.notification.ChatNotifier;
import net.alcaris.plugin.core.service.DataSyncManager;
import org.bukkit.Bukkit;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class APIWebSocketClient extends WebSocketClient {
    private final AlcarisCore plugin;
    private final ReconnectHandler reconnectHandler;
    private volatile boolean manualClose = false;

    public APIWebSocketClient(URI serverUri, String apiKey, AlcarisCore plugin) {
        super(serverUri, new Draft_6455(), buildHeaders(apiKey), 0);
        this.plugin = plugin;
        this.reconnectHandler = new ReconnectHandler(this, plugin);
        this.setConnectionLostTimeout(30);
    }

    private static Map<String, String> buildHeaders(String apiKey) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + apiKey);
        return headers;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        plugin.getLogger().info("WebSocket connected");
        reconnectHandler.reset();
        manualClose = false;
    }

    @Override
    public void onMessage(String message) {
        try {
            JsonObject json = JsonParser.parseString(message).getAsJsonObject();

            String type = json.has("type") ? json.get("type").getAsString() : "unknown";
            String category = json.has("category") ? json.get("category").getAsString() : "unknown";
            String actor = json.has("actor") ? json.get("actor").getAsString() : "unknown";
            String id = json.has("id") ? json.get("id").getAsString() : "unknown";
            String platform = json.has("platform") ? json.get("platform").getAsString() : "unknown";

            plugin.getLogger().info(String.format("<%s> %s by %s (%s)",
                    category, capitalize(type), actor, capitalize(platform)));

            ChatNotifier.broadcast( platform, category, type, id, actor);

            DataSyncManager manager = plugin.getDataSyncManager();
            manager.reload(category)
                    .exceptionally(ex -> {
                        plugin.getLogger().severe("Failed to reload " + category + ": " + ex.getMessage());
                        return null;
                    });
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to parse WebSocket message: " + e.getMessage());
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        plugin.getLogger().warning("WebSocket closed: " + reason + " (code: " + code + ")");

        if (!manualClose && remote) {
            reconnectHandler.scheduleReconnect();
        }
    }

    @Override
    public void onError(Exception ex) {
        plugin.getLogger().severe("WebSocket error: " + ex.getMessage());
    }

    @Override
    public void close() {
        manualClose = true;
        super.close();
    }

    private String capitalize(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    private static class ReconnectHandler {
        private final APIWebSocketClient client;
        private final AlcarisCore plugin;
        private final AtomicInteger attempt = new AtomicInteger(0);
        private static final int MAX_ATTEMPTS = 10;
        private static final long[] BACKOFF_DELAYS = {1, 2, 5, 10, 30, 60, 120, 300, 600, 1800};

        ReconnectHandler(APIWebSocketClient client, AlcarisCore plugin) {
            this.client = client;
            this.plugin = plugin;
        }

        void scheduleReconnect() {
            int current = attempt.get();
            if (current >= MAX_ATTEMPTS) {
                plugin.getLogger().severe("WebSocket reconnection failed after " + MAX_ATTEMPTS + " attempts");
                plugin.getLogger().severe("Server will run without real-time updates");
                return;
            }

            long delay = BACKOFF_DELAYS[Math.min(current, BACKOFF_DELAYS.length - 1)];
            plugin.getLogger().info("Reconnecting in " + delay + " seconds... (attempt " + (current + 1) + "/" + MAX_ATTEMPTS + ")");

            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                try {
                    client.reconnectBlocking();
                    plugin.getLogger().info("WebSocket reconnected");
                } catch (Exception e) {
                    plugin.getLogger().warning("Reconnection failed: " + e.getMessage());
                    attempt.incrementAndGet();
                    scheduleReconnect();
                }
            }, delay * 20L);
        }

        void reset() {
            attempt.set(0);
        }
    }
}

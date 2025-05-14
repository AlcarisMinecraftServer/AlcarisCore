package net.alcaris.plugin.core.lib;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.alcaris.plugin.core.AlcarisCore;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class APIWebSocketClient extends WebSocketClient {

    private final AlcarisCore plugin;

    public APIWebSocketClient(URI serverUri, String apiKey, AlcarisCore plugin) {
        super(serverUri, new Draft_6455(), buildHeaders(apiKey), 0);
        this.plugin = plugin;
    }

    private static Map<String, String> buildHeaders(String apiKey) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + apiKey);
        return headers;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        plugin.getLogger().info("WebSocket connected.");
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

            plugin.getLogger().info(String.format("<%s> %s by %s (%s)", category, capitalize(type), actor, capitalize(platform)));

            ChatNotifier.broadcast(
                    plugin,
                    platform,
                    category,
                    type,
                    id,
                    actor
            );

            DataSyncManager manager = plugin.getDataSyncManager();
            manager.reloadAll();
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to parse WebSocket message: " + e.getMessage());
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        plugin.getLogger().warning("WebSocket closed: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        plugin.getLogger().severe("WebSocket error: " + ex.getMessage());
    }

    private String capitalize(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
}

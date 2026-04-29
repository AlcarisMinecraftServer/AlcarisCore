package net.alcaris.plugin.core.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.alcaris.plugin.core.AlcarisCore;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.alcaris.plugin.core.model.item.ItemBaseModel;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class ApiClient {
    private final OkHttpClient client;
    private final Gson gson = new Gson();
    private final String apiUrl;
    private final String apiKey;
    private final AlcarisCore plugin;

    public ApiClient(String apiUrl, String apiKey, AlcarisCore plugin) {
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.plugin = plugin;

        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(5, 5, TimeUnit.MINUTES))
                .retryOnConnectionFailure(true)
                .build();
    }

    public CompletableFuture<List<ItemBaseModel>> fetchItemsAsync() {
        Request request = new Request.Builder()
                .url(apiUrl + "/items")
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        CompletableFuture<List<ItemBaseModel>> future = new CompletableFuture<>();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                plugin.getLogger().severe("API Request Failed: " + e.getMessage());
                future.completeExceptionally(new ApiException("Failed to fetch items from API", e));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try (ResponseBody body = response.body()) {
                    if (!response.isSuccessful()) {
                        String errorMsg = "API returned error: " + response.code();
                        plugin.getLogger().severe(errorMsg);
                        future.completeExceptionally(new ApiException(errorMsg));
                        return;
                    }

                    if (body == null) {
                        future.completeExceptionally(new ApiException("API response body is null"));
                        return;
                    }

                    String bodyString = body.string();
                    JsonObject json = gson.fromJson(bodyString, JsonObject.class);

                    if (!json.has("data")) {
                        future.completeExceptionally(new ApiException("API response missing 'data' field"));
                        return;
                    }

                    JsonArray dataArray = json.getAsJsonArray("data");
                    ItemBaseModel[] items = gson.fromJson(dataArray, ItemBaseModel[].class);

                    future.complete(Arrays.asList(items));

                } catch (Exception e) {
                    plugin.getLogger().severe("Failed to parse API response: " + e.getMessage());
                    future.completeExceptionally(new ApiException("Failed to parse API response", e));
                }
            }
        });

        return future;
    }

    public void shutdown() {
        client.dispatcher().executorService().shutdown();
        client.connectionPool().evictAll();
    }
}

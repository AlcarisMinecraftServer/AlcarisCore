package net.alcaris.plugin.core.lib;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;

import net.alcaris.plugin.core.model.item.ItemBaseModel;
import org.jetbrains.annotations.NotNull;

public class ApiClient {
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    private final String apiUrl;
    private final String apiKey;

    public ApiClient(String apiUrl, String apiKey) {
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
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
                future.complete(Collections.emptyList());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    future.complete(Collections.emptyList());
                    return;
                }

                if (response.body() == null) {
                    future.complete(Collections.emptyList());
                    return;
                }

                String body = response.body().string();
                JsonObject json = gson.fromJson(body, JsonObject.class);
                JsonArray dataArray = json.getAsJsonArray("data");

                ItemBaseModel[] items = gson.fromJson(dataArray, ItemBaseModel[].class);
                future.complete(Arrays.asList(items));
            }
        });

        return future;
    }
}

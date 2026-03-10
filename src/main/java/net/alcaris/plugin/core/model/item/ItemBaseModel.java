package net.alcaris.plugin.core.model.item;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class ItemBaseModel {
    private static final Gson GSON = new Gson();

    private String id;
    private ItemCategory category;
    private long version;
    private String name;
    private List<String> lore;
    private int rarity;
    private int max_stack;
    private int custom_model_data;
    private Price price;
    private List<Tag> tags;
    private Object data;

    public String getId() {
        return id;
    }
    public ItemCategory getCategory() {
        return category;
    }
    public long getVersion() {
        return version;
    }
    public String getName() {
        return name;
    }
    public List<String> getLore() {
        return lore;
    }
    public int getRarity() {
        return rarity;
    }
    public int getMaxStack() {
        return max_stack;
    }
    public int getCustomModelData() {
        return custom_model_data;
    }
    public Price getPrice() {
        return price;
    }
    public List<Tag> getTags() {
        return tags;
    }
    public Object getData() {
        return data;
    }

    /**
     * Returns the item-specific {@code data} normalized to a Gson {@link JsonElement}.
     * <p>
     * This does not change the existing {@link #getData()} contract and is safe for
     * callers that prefer JSON access without introducing a breaking change.
     * <p>
     * Typical usage in dependent plugins (e.g., alcarisitems):
     * <pre>{@code
     * JsonObject json = item.getDataAsJsonObject();
     * if (json != null) {
     *     int red = json.has("red") ? json.get("red").getAsInt() : 0;
     * }
     * }</pre>
     *
     * @return {@code JsonElement} view of data, or {@code null} when no data is present
     */
    public JsonElement getDataJson() {
        if (data == null) return null;
        if (data instanceof JsonElement json) return json;
        return GSON.toJsonTree(data);
    }

    /**
     * Convenience accessor to view {@code data} as a {@link JsonObject}.
     * Returns {@code null} when {@code data} is absent or not an object.
     */
    public JsonObject getDataAsJsonObject() {
        JsonElement el = getDataJson();
        return (el != null && el.isJsonObject()) ? el.getAsJsonObject() : null;
    }

    @SuppressWarnings("unchecked")
    public <T> T getTypedData(Class<T> clazz) {
        if (data == null) return null;

        if (clazz.isInstance(data)) return (T) data;

        if (data instanceof JsonElement) {
            return GSON.fromJson((JsonElement) data, clazz);
        }

        return GSON.fromJson(GSON.toJson(data), clazz);
    }

    public Object getAutoTypedData() {
        if (data == null || category == null) return null;

        return switch (category) {
            case FOOD -> getTypedData(ItemFoodModel.class);
            case TOOL -> getTypedData(ItemToolModel.class);
            case WEAPON -> getTypedData(ItemWeaponModel.class);
            case ARMOR -> getTypedData(ItemArmorModel.class);
            case MATERIAL -> getTypedData(ItemMaterialModel.class);
            default -> data;
        };
    }

    public static class Price {
        private int buy;
        private int sell;
        private boolean can_sell;

        public int getBuy() { return buy; }
        public int getSell() { return sell; }
        public boolean getCanSell() { return can_sell; }
    }

    public static class Tag {
        private String label;
        private String color;

        public String getLabel() { return label; }
        public String getColor() { return color; }
    }

    public enum ItemCategory {
        @SerializedName("food") FOOD,
        @SerializedName("tool") TOOL,
        @SerializedName("armor") ARMOR,
        @SerializedName("weapon") WEAPON,
        @SerializedName("material") MATERIAL;

        public static ItemCategory fromString(String s) {
            return ItemCategory.valueOf(s.toUpperCase());
        }
    }
}

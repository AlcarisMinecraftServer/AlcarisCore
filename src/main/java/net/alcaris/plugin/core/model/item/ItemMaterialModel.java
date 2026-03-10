package net.alcaris.plugin.core.model.item;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class ItemMaterialModel extends ArrayList<String> {
    /**
     * Typed view of MATERIAL item data.
     * <p>
     * MATERIAL data is stored as a JSON array of {@code "key:value"} entries,
     * for example:
     * <pre>{@code
     * ["red:10", "blue:1", "purple:3"]
     * }</pre>
     * Retrieve via:
     * <pre>{@code
     * ItemMaterialModel m = item.getTypedData(ItemMaterialModel.class);
     * // or:
     * JsonElement json = item.getDataJson();
     * }</pre>
     * Missing colors are treated as {@code 0}.
     */

    public List<String> getEntries() {
        return List.copyOf(this);
    }

    public Map<String, Integer> asMap() {
        Map<String, Integer> values = new LinkedHashMap<>();
        for (String entry : this) {
            ParsedEntry parsed = parseEntry(entry);
            if (parsed != null) {
                values.put(parsed.key(), parsed.value());
            }
        }
        return values;
    }

    public int getValue(String key) {
        Integer value = asMap().get(key);
        return value != null ? value : 0;
    }

    public int getRed() { return getValue("red"); }
    public int getOrange() { return getValue("orange"); }
    public int getYellow() { return getValue("yellow"); }
    public int getLime() { return getValue("lime"); }
    public int getGreen() { return getValue("green"); }
    public int getCyan() { return getValue("cyan"); }
    public int getAqua() { return getValue("aqua"); }
    public int getBlue() { return getValue("blue"); }
    public int getPink() { return getValue("pink"); }
    public int getPurple() { return getValue("purple"); }
    public int getBlack() { return getValue("black"); }
    public int getGray() { return getValue("gray"); }
    public int getWhite() { return getValue("white"); }

    private ParsedEntry parseEntry(String entry) {
        if (entry == null) {
            return null;
        }

        int separatorIndex = entry.indexOf(':');
        if (separatorIndex <= 0 || separatorIndex == entry.length() - 1) {
            return null;
        }

        String key = entry.substring(0, separatorIndex).trim();
        String rawValue = entry.substring(separatorIndex + 1).trim();
        if (key.isEmpty()) {
            return null;
        }

        try {
            return new ParsedEntry(key, Integer.parseInt(rawValue));
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private record ParsedEntry(String key, int value) {
    }
}


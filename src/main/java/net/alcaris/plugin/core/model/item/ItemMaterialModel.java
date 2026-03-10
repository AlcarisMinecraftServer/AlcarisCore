package net.alcaris.plugin.core.model.item;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class ItemMaterialModel extends LinkedHashMap<String, List<String>> {
    /**
     * Typed view of MATERIAL item data.
     * <p>
     * MATERIAL data is stored as a keyed JSON object whose values are arrays of
     * {@code "color:value"} entries, for example:
     * <pre>{@code
     * {
     *   "magic_materials": ["red:2", "blue:1"]
     * }
     * }</pre>
     */

    public List<String> getEntries(String key) {
        List<String> entries = get(key);
        return entries != null ? List.copyOf(entries) : List.of();
    }

    public List<String> getMagicMaterials() {
        return getEntries("magic_materials");
    }

    public Map<String, Integer> asMap(String key) {
        Map<String, Integer> values = new LinkedHashMap<>();
        for (String entry : getEntries(key)) {
            ParsedEntry parsed = parseEntry(entry);
            if (parsed != null) {
                values.put(parsed.key(), parsed.value());
            }
        }
        return values;
    }

    public Map<String, Integer> getMagicMaterialMap() {
        return asMap("magic_materials");
    }

    public int getValue(String key, String color) {
        Integer value = asMap(key).get(color);
        return value != null ? value : 0;
    }

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


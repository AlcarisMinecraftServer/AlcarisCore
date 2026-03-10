package net.alcaris.plugin.core.model.item;

@SuppressWarnings("unused")
public class ItemMaterialModel {
    /**
     * Typed view of MATERIAL item data.
     * <p>
     * Retrieve via:
     * <pre>{@code
     * ItemMaterialModel m = item.getTypedData(ItemMaterialModel.class);
     * // or:
     * JsonObject json = item.getDataAsJsonObject();
     * }</pre>
     * Maps 13 color score integers: red, orange, yellow, lime, green, cyan,
     * aqua, blue, pink, purple, black, gray, white.
     */
    // Color score fields, mapped directly from JSON (e.g., "red": 3)
    private int red;
    private int orange;
    private int yellow;
    private int lime;
    private int green;
    private int cyan;
    private int aqua;
    private int blue;
    private int pink;
    private int purple;
    private int black;
    private int gray;
    private int white;

    public int getRed() { return red; }
    public int getOrange() { return orange; }
    public int getYellow() { return yellow; }
    public int getLime() { return lime; }
    public int getGreen() { return green; }
    public int getCyan() { return cyan; }
    public int getAqua() { return aqua; }
    public int getBlue() { return blue; }
    public int getPink() { return pink; }
    public int getPurple() { return purple; }
    public int getBlack() { return black; }
    public int getGray() { return gray; }
    public int getWhite() { return white; }
}


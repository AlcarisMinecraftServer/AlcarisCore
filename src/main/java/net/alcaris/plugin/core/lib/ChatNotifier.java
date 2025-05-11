package net.alcaris.plugin.core.lib;

import net.alcaris.plugin.core.AlcarisCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Map;

public final class ChatNotifier {

    private ChatNotifier() {}

    private static final Map<String, String> TYPE_MAP = Map.of(
            "create",  "Created",
            "created", "Created",
            "update",  "Updated",
            "updated", "Updated",
            "delete",  "Deleted",
            "deleted", "Deleted"
    );

    public static void broadcast(AlcarisCore plugin,
                                 String platform,
                                 String category,
                                 String type,
                                 String id,
                                 String actor) {

        String pf = (platform == null || platform.isBlank()) ? "Unknown" : platform.toUpperCase(Locale.ROOT);

        String typeNorm = TYPE_MAP.getOrDefault(
                type == null ? "" : type.toLowerCase(Locale.ROOT),
                capitalize(type)
        );

        Component message = Component.text("[" + pf + "] ").color(NamedTextColor.BLUE)
                .append(Component.text("(" + category + ":" + id + ") ").color(NamedTextColor.GRAY))
                .append(Component.text(typeNorm + " ").color(NamedTextColor.GRAY))
                .append(Component.text("by ").color(NamedTextColor.GRAY))
                .append(Component.text(actor).color(NamedTextColor.WHITE));

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.isOp()) {
                p.sendMessage(message);
            }
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.isBlank()) return "";
        return s.substring(0, 1).toUpperCase(Locale.ROOT) +
                s.substring(1).toLowerCase(Locale.ROOT);
    }
}

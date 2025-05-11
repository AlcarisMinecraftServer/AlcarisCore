package net.alcaris.plugin.core.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.BiFunction;

public abstract class SubCommand {
    private final String permission;
    private final boolean playerOnly;
    private final int minArgs;

    protected SubCommand(String permission, boolean playerOnly, int minArgs) {
        this.permission = permission;
        this.playerOnly = playerOnly;
        this.minArgs = minArgs;
    }

    public boolean execute(CommandSender sender, String[] args) {
        if (playerOnly && !(sender instanceof Player)) {
            sender.sendMessage("This command is only executable by players.");
            return true;
        }

        if (permission != null && !sender.hasPermission(permission)) {
            sender.sendMessage("You don't have permission.");
            return true;
        }

        if (args.length < minArgs) {
            sender.sendMessage("Not enough arguments.");
            return true;
        }

        return onExecute(sender, args);
    }

    protected abstract boolean onExecute(CommandSender sender, String[] args);

    public abstract List<String> tabComplete(CommandSender sender, String[] args);

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String permission = null;
        private boolean playerOnly = false;
        private int minArgs = 0;
        private BiFunction<CommandSender, String[], Boolean> executor;
        private BiFunction<CommandSender, String[], List<String>> tabCompleter = (s, a) -> List.of();

        public Builder permission(String permission) {
            this.permission = permission;
            return this;
        }

        public Builder playerOnly() {
            this.playerOnly = true;
            return this;
        }

        public Builder minArgs(int minArgs) {
            this.minArgs = minArgs;
            return this;
        }

        public Builder executor(BiFunction<CommandSender, String[], Boolean> executor) {
            this.executor = executor;
            return this;
        }

        public Builder tabCompleter(BiFunction<CommandSender, String[], List<String>> tabCompleter) {
            this.tabCompleter = tabCompleter;
            return this;
        }

        public SubCommand build() {
            return new SubCommand(permission, playerOnly, minArgs) {
                @Override
                protected boolean onExecute(CommandSender sender, String[] args) {
                    return executor.apply(sender, args);
                }

                @Override
                public List<String> tabComplete(CommandSender sender, String[] args) {
                    return tabCompleter.apply(sender, args);
                }
            };
        }
    }
}

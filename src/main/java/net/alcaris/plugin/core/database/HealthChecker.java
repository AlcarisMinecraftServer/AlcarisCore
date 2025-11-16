package net.alcaris.plugin.core.database;

import net.alcaris.plugin.core.AlcarisCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.atomic.AtomicInteger;

public class HealthChecker {
    private final AlcarisCore plugin;
    private final DatabaseManager databaseManager;
    private BukkitTask healthCheckTask;

    private static final long CHECK_INTERVAL_TICKS = 20L * 30;
    private static final int MAX_CONSECUTIVE_FAILURES = 3;

    private final AtomicInteger consecutiveFailures = new AtomicInteger(0);
    private volatile boolean running = false;

    public HealthChecker(AlcarisCore plugin, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
    }

    public void start() {
        if (running) {
            plugin.getLogger().warning("Health checker is already running");
            return;
        }

        this.running = true;
        this.healthCheckTask = Bukkit.getScheduler().runTaskTimerAsynchronously(
                plugin,
                this::performHealthCheck,
                CHECK_INTERVAL_TICKS,
                CHECK_INTERVAL_TICKS
        );

        plugin.getLogger().info("Database health checker started (interval: 30 seconds)");
    }

    public void stop() {
        if (healthCheckTask != null) {
            healthCheckTask.cancel();
            healthCheckTask = null;
        }
        this.running = false;
    }

    private void performHealthCheck() {
        try {
            boolean success = databaseManager.testConnection();

            if (success) {
                int previousFailures = consecutiveFailures.getAndSet(0);

                if (previousFailures > 0) {
                    plugin.getLogger().info("Database connection restored");
                    databaseManager.markHealthy();
                }
            } else {
                int failures = consecutiveFailures.incrementAndGet();

                plugin.getLogger().severe("Database health check failed (" + failures + "/" + MAX_CONSECUTIVE_FAILURES + ")");

                if (failures >= MAX_CONSECUTIVE_FAILURES) {
                    handleCriticalFailure();
                } else {
                    databaseManager.markUnhealthy();
                }
            }

        } catch (Exception e) {
            int failures = consecutiveFailures.incrementAndGet();
            plugin.getLogger().severe("Database health check error: " + e.getMessage());
            plugin.getLogger().severe("Failures: " + failures + "/" + MAX_CONSECUTIVE_FAILURES);

            if (failures >= MAX_CONSECUTIVE_FAILURES) {
                handleCriticalFailure();
            }
        }
    }

    private void handleCriticalFailure() {
        plugin.getLogger().severe("========================================");
        plugin.getLogger().severe("CRITICAL: Database connection lost");
        plugin.getLogger().severe("Server will shut down for emergency maintenance");
        plugin.getLogger().severe("========================================");

        databaseManager.markUnhealthy();

        Bukkit.getScheduler().runTask(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.kick(net.kyori.adventure.text.Component.text(
                        "Server is undergoing emergency maintenance.\n" +
                                "Database connection has been lost.\n" +
                                "Please try again later."
                ));
            }

            plugin.getLogger().severe("All players have been disconnected");

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                plugin.getLogger().severe("Shutting down server...");
                Bukkit.getServer().shutdown();
            }, 100L);
        });
    }

    public int getConsecutiveFailures() {
        return consecutiveFailures.get();
    }

    public boolean isRunning() {
        return running;
    }
}
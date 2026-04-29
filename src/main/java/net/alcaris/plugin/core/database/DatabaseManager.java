package net.alcaris.plugin.core.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.alcaris.plugin.core.AlcarisCore;
import net.alcaris.plugin.core.config.CoreConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class DatabaseManager {
    private final AlcarisCore plugin;
    private final CoreConfig config;
    private HikariDataSource dataSource;
    private HealthChecker healthChecker;
    private volatile boolean healthy = false;

    public DatabaseManager(AlcarisCore plugin, CoreConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    public void initialize() throws SQLException {
        try {
            HikariConfig hikariConfig = new HikariConfig();

            String jdbcUrl = String.format(
                    "jdbc:mysql://%s:%d/%s?useSSL=%b&allowPublicKeyRetrieval=%b&serverTimezone=Asia/Tokyo",
                    config.getDbHost(),
                    config.getDbPort(),
                    config.getDbName(),
                    config.isDbUseSSL(),
                    !config.isDbUseSSL()
            );

            hikariConfig.setJdbcUrl(jdbcUrl);
            hikariConfig.setUsername(config.getDbUser());
            hikariConfig.setPassword(config.getDbPassword());

            hikariConfig.setMaximumPoolSize(config.getDbPoolSize());
            hikariConfig.setMinimumIdle(Math.max(2, config.getDbPoolSize() / 2));
            hikariConfig.setConnectionTimeout(TimeUnit.SECONDS.toMillis(10));
            hikariConfig.setIdleTimeout(TimeUnit.MINUTES.toMillis(10));
            hikariConfig.setMaxLifetime(TimeUnit.MINUTES.toMillis(30));
            hikariConfig.setLeakDetectionThreshold(TimeUnit.SECONDS.toMillis(60));

            hikariConfig.setPoolName("AlcarisCore-Pool");

            hikariConfig.setConnectionTestQuery("SELECT 1");

            hikariConfig.setAutoCommit(true);

            this.dataSource = new HikariDataSource(hikariConfig);

            try (Connection conn = dataSource.getConnection()) {
                if (!conn.isValid(5)) {
                    throw new SQLException("Database connection validation failed");
                }
            }

            this.healthy = true;
            plugin.getLogger().info("Database connection pool initialized");
            plugin.getLogger().info("  Host: " + config.getDbHost() + ":" + config.getDbPort());
            plugin.getLogger().info("  Database: " + config.getDbName());
            plugin.getLogger().info("  Pool size: " + config.getDbPoolSize());

            this.healthChecker = new HealthChecker(plugin, this);
            this.healthChecker.start();

        } catch (Exception e) {
            this.healthy = false;
            plugin.getLogger().severe("Failed to initialize database connection");
            plugin.getLogger().severe("Reason: " + e.getMessage());
            throw new SQLException("Database initialization failed", e);
        }
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            throw new SQLException("Database connection pool is not initialized or closed");
        }

        if (!healthy) {
            throw new SQLException("Database is marked as unhealthy");
        }

        return dataSource.getConnection();
    }

    @SuppressWarnings("SqlNoDataSourceInspection")
    public boolean testConnection() {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT 1");
             ResultSet rs = stmt.executeQuery()) {

            return rs.next() && rs.getInt(1) == 1;

        } catch (SQLException e) {
            plugin.getLogger().warning("Database connection test failed: " + e.getMessage());
            return false;
        }
    }

    public void shutdown() {
        if (healthChecker != null) {
            healthChecker.stop();
            plugin.getLogger().info("Database health checker stopped");
        }

        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            plugin.getLogger().info("Database connection pool closed");
        }

        this.healthy = false;
    }

    public boolean isHealthy() {
        return healthy;
    }

    public void markUnhealthy() {
        this.healthy = false;
    }

    public void markHealthy() {
        this.healthy = true;
    }

    public String getPoolStats() {
        if (dataSource == null || dataSource.isClosed()) {
            return "Pool: Not initialized";
        }

        return String.format(
                "Pool: Active=%d, Idle=%d, Waiting=%d, Total=%d",
                dataSource.getHikariPoolMXBean().getActiveConnections(),
                dataSource.getHikariPoolMXBean().getIdleConnections(),
                dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection(),
                dataSource.getHikariPoolMXBean().getTotalConnections()
        );
    }
}
package net.alcaris.plugin.core.config;

import org.bukkit.configuration.file.FileConfiguration;

public class CoreConfig {
    private final String apiUrl;
    private final String apiKey;
    private final String dbHost;
    private final int dbPort;
    private final String dbName;
    private final String dbUser;
    private final String dbPassword;
    private final int dbPoolSize;

    public CoreConfig(FileConfiguration config) {
        this.apiUrl = config.getString("general.apiUrl", "");
        this.apiKey = config.getString("general.apiKey", "");

        this.dbHost = config.getString("database.host", "localhost");
        this.dbPort = config.getInt("database.port", 3306);
        this.dbName = config.getString("database.name", "alcaris");
        this.dbUser = config.getString("database.user", "root");
        this.dbPassword = config.getString("database.password", "");
        this.dbPoolSize = config.getInt("database.poolSize", 10);
    }

    public boolean validate() {
        if (apiUrl == null || apiUrl.isBlank()) return false;
        if (apiKey == null || apiKey.isBlank()) return false;
        if (!apiUrl.startsWith("http://") && !apiUrl.startsWith("https://")) return false;

        return true;
    }

    // Getters
    public String getApiUrl() { return apiUrl; }
    public String getApiKey() { return apiKey; }
    public String getDbHost() { return dbHost; }
    public int getDbPort() { return dbPort; }
    public String getDbName() { return dbName; }
    public String getDbUser() { return dbUser; }
    public String getDbPassword() { return dbPassword; }
    public int getDbPoolSize() { return dbPoolSize; }
}
package pl.batek.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DatabaseManager {
    private HikariDataSource dataSource;
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;

    public record FreeItemsData(long trapCooldown, long keysCooldown, boolean oneTimeClaimed) {}
    public record TopEntry(UUID uuid, double value) {}

    public DatabaseManager(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public void connect() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&autoReconnect=true&createDatabaseIfNotExist=true");
        config.setUsername(username);
        config.setPassword(password);

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        this.dataSource = new HikariDataSource(config);
        createTables();
    }

    public void disconnect() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public HikariDataSource getDataSource() {
        return this.dataSource;
    }

    private void createTables() {
        try (Connection conn = dataSource.getConnection()) {
            // Free Items
            conn.prepareStatement("CREATE TABLE IF NOT EXISTS free_items (uuid VARCHAR(36) PRIMARY KEY, trap_cooldown BIGINT, keys_cooldown BIGINT, one_time_claimed BOOLEAN)").execute();
            // Codes
            conn.prepareStatement("CREATE TABLE IF NOT EXISTS used_codes (uuid VARCHAR(36), code_name VARCHAR(64), PRIMARY KEY (uuid, code_name))").execute();
            // Economy
            conn.prepareStatement("CREATE TABLE IF NOT EXISTS economy (uuid VARCHAR(36) PRIMARY KEY, balance DOUBLE)").execute();

            // Player stats
            conn.prepareStatement("CREATE TABLE IF NOT EXISTS player_stats (uuid VARCHAR(36) PRIMARY KEY, playtime BIGINT, time_coins BIGINT DEFAULT 0)").execute();

            // Zabezpieczenie na wypadek aktualizacji starej tabeli stats
            try {
                conn.prepareStatement("ALTER TABLE player_stats ADD COLUMN time_coins BIGINT DEFAULT 0").execute();
            } catch (SQLException ignored) {}

            // NOWA TABELA Discord Rewards (zapisuje ID Discorda ORAZ Nick)
            conn.prepareStatement("CREATE TABLE IF NOT EXISTS discord_rewards_users (discord_id VARCHAR(32) PRIMARY KEY, nick VARCHAR(16) UNIQUE)").execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- DISCORD REWARDS ---
    public void loadDiscordRewardsSync(Set<String> discordIds, Set<String> nicks) {
        try (Connection conn = dataSource.getConnection(); ResultSet rs = conn.prepareStatement("SELECT discord_id, nick FROM discord_rewards_users").executeQuery()) {
            while (rs.next()) {
                discordIds.add(rs.getString("discord_id"));
                nicks.add(rs.getString("nick").toLowerCase());
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void setDiscordRewardClaimedAsync(String discordId, String nick) {
        CompletableFuture.runAsync(() -> {
            try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement("INSERT IGNORE INTO discord_rewards_users (discord_id, nick) VALUES (?, ?)")) {
                stmt.setString(1, discordId);
                stmt.setString(2, nick.toLowerCase());
                stmt.executeUpdate();
            } catch (SQLException e) { e.printStackTrace(); }
        });
    }

    // --- CODES ---
    public CompletableFuture<List<String>> getUsedCodesAsync(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> used = new ArrayList<>();
            String query = "SELECT code_name FROM used_codes WHERE uuid = ?";
            try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, uuid.toString());
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) used.add(rs.getString("code_name"));
                }
            } catch (SQLException e) { e.printStackTrace(); }
            return used;
        });
    }

    public void setCodeUsedAsync(UUID uuid, String codeName) {
        CompletableFuture.runAsync(() -> {
            try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement("INSERT IGNORE INTO used_codes (uuid, code_name) VALUES (?, ?)")) {
                stmt.setString(1, uuid.toString());
                stmt.setString(2, codeName.toLowerCase());
                stmt.executeUpdate();
            } catch (SQLException e) { e.printStackTrace(); }
        });
    }

    // --- FREE ITEMS ---
    public void savePlayerData(UUID uuid, long trap, long keys, boolean oneTime) {
        String query = "INSERT INTO free_items (uuid, trap_cooldown, keys_cooldown, one_time_claimed) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE trap_cooldown = ?, keys_cooldown = ?, one_time_claimed = ?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, uuid.toString());
            stmt.setLong(2, trap); stmt.setLong(3, keys); stmt.setBoolean(4, oneTime);
            stmt.setLong(5, trap); stmt.setLong(6, keys); stmt.setBoolean(7, oneTime);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public CompletableFuture<FreeItemsData> getPlayerDataAsync(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT trap_cooldown, keys_cooldown, one_time_claimed FROM free_items WHERE uuid = ?")) {
                stmt.setString(1, uuid.toString());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) return new FreeItemsData(rs.getLong("trap_cooldown"), rs.getLong("keys_cooldown"), rs.getBoolean("one_time_claimed"));
                }
            } catch (SQLException e) { e.printStackTrace(); }
            return null;
        });
    }

    // --- ECONOMY ---
    public boolean hasAccountSync(UUID uuid) {
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT 1 FROM economy WHERE uuid = ?")) {
            stmt.setString(1, uuid.toString());
            try (ResultSet rs = stmt.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public double getBalanceSync(UUID uuid) {
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT balance FROM economy WHERE uuid = ?")) {
            stmt.setString(1, uuid.toString());
            try (ResultSet rs = stmt.executeQuery()) { if (rs.next()) return rs.getDouble("balance"); }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }

    public void setBalanceAsync(UUID uuid, double balance) {
        CompletableFuture.runAsync(() -> {
            try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement("INSERT INTO economy (uuid, balance) VALUES (?, ?) ON DUPLICATE KEY UPDATE balance = ?")) {
                stmt.setString(1, uuid.toString());
                stmt.setDouble(2, balance); stmt.setDouble(3, balance);
                stmt.executeUpdate();
            } catch (SQLException e) { e.printStackTrace(); }
        });
    }

    // --- STATS & TOPS ---
    public long[] getPlaytimeAndCoinsSync(UUID uuid) {
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT playtime, time_coins FROM player_stats WHERE uuid = ?")) {
            stmt.setString(1, uuid.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return new long[]{rs.getLong("playtime"), rs.getLong("time_coins")};
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return new long[]{0L, 0L};
    }

    public void savePlaytimeSync(UUID uuid, long playtime, long timeCoins) {
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement("INSERT INTO player_stats (uuid, playtime, time_coins) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE playtime = ?, time_coins = ?")) {
            stmt.setString(1, uuid.toString());
            stmt.setLong(2, playtime); stmt.setLong(3, timeCoins);
            stmt.setLong(4, playtime); stmt.setLong(5, timeCoins);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<TopEntry> getTopEconomySync() {
        List<TopEntry> top = new ArrayList<>();
        try (Connection conn = dataSource.getConnection(); ResultSet rs = conn.prepareStatement("SELECT uuid, balance FROM economy ORDER BY balance DESC LIMIT 10").executeQuery()) {
            while (rs.next()) top.add(new TopEntry(UUID.fromString(rs.getString("uuid")), rs.getDouble("balance")));
        } catch (SQLException e) { e.printStackTrace(); }
        return top;
    }

    public List<TopEntry> getTopTimeSync() {
        List<TopEntry> top = new ArrayList<>();
        try (Connection conn = dataSource.getConnection(); ResultSet rs = conn.prepareStatement("SELECT uuid, playtime FROM player_stats ORDER BY playtime DESC LIMIT 10").executeQuery()) {
            while (rs.next()) top.add(new TopEntry(UUID.fromString(rs.getString("uuid")), rs.getDouble("playtime")));
        } catch (SQLException e) { e.printStackTrace(); }
        return top;
    }

    public CompletableFuture<Long> getTimeCoinsAsync(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT time_coins FROM player_stats WHERE uuid = ?")) {
                stmt.setString(1, uuid.toString());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) return rs.getLong("time_coins");
                }
            } catch (SQLException e) { e.printStackTrace(); }
            return 0L;
        });
    }

    public void setTimeCoinsAsync(UUID uuid, long timeCoins) {
        CompletableFuture.runAsync(() -> {
            String query = "INSERT INTO player_stats (uuid, playtime, time_coins) VALUES (?, 0, ?) ON DUPLICATE KEY UPDATE time_coins = ?";
            try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, uuid.toString());
                stmt.setLong(2, timeCoins);
                stmt.setLong(3, timeCoins);
                stmt.executeUpdate();
            } catch (SQLException e) { e.printStackTrace(); }
        });
    }

    public void savePlaytimeAndCoinsAsync(UUID uuid, long playtime, long timeCoins) {
        CompletableFuture.runAsync(() -> {
            String query = "INSERT INTO player_stats (uuid, playtime, time_coins) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE playtime = ?, time_coins = ?";
            try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, uuid.toString());
                stmt.setLong(2, playtime);
                stmt.setLong(3, timeCoins);
                stmt.setLong(4, playtime);
                stmt.setLong(5, timeCoins);
                stmt.executeUpdate();
            } catch (SQLException e) { e.printStackTrace(); }
        });
    }
}
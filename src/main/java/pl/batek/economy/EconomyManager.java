package pl.batek.economy;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import pl.batek.database.DatabaseManager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EconomyManager {
    private final JavaPlugin plugin;
    private final DatabaseManager db;
    private final Map<UUID, Double> cache = new ConcurrentHashMap<>();

    public EconomyManager(JavaPlugin plugin, DatabaseManager db) {
        this.plugin = plugin;
        this.db = db;
    }

    public void loadPlayer(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            double balance = db.getBalanceSync(uuid);
            cache.put(uuid, balance);
        });
    }

    public void unloadPlayer(UUID uuid) {
        Double balance = cache.remove(uuid);
        if (balance != null) {
            db.setBalanceAsync(uuid, balance);
        }
    }

    public boolean hasAccount(OfflinePlayer player) {
        if (cache.containsKey(player.getUniqueId())) return true;
        return db.hasAccountSync(player.getUniqueId());
    }

    public double getBalance(OfflinePlayer player) {
        if (cache.containsKey(player.getUniqueId())) {
            return cache.get(player.getUniqueId());
        }
        return db.getBalanceSync(player.getUniqueId());
    }

    public void setBalance(OfflinePlayer player, double amount) {
        if (player.isOnline()) {
            cache.put(player.getUniqueId(), amount);
        }
        db.setBalanceAsync(player.getUniqueId(), amount);
    }
}
package pl.batek.manager;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import pl.batek.database.DatabaseManager;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FreeItemsManager {

    // Używamy "Concurrent", aby zapobiec błędom przy asynchronicznym zapisie/odczycie
    private final Map<UUID, Long> trapCooldowns = new ConcurrentHashMap<>();
    private final Map<UUID, Long> keysCooldowns = new ConcurrentHashMap<>();
    private final Set<UUID> oneTimeClaimed = ConcurrentHashMap.newKeySet();

    private final Plugin plugin;
    private final DatabaseManager databaseManager;

    public FreeItemsManager(Plugin plugin, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
    }

    // --- LOGIKA COOLDOWNÓW (RAM) ---

    public long getTrapCooldown(UUID uuid) {
        return trapCooldowns.getOrDefault(uuid, 0L) - System.currentTimeMillis();
    }

    public void setTrapCooldown(UUID uuid) {
        long expireTime = System.currentTimeMillis() + (60L * 1000L);
        trapCooldowns.put(uuid, expireTime);
        saveToDatabaseAsync(uuid);
    }

    public long getKeysCooldown(UUID uuid) {
        return keysCooldowns.getOrDefault(uuid, 0L) - System.currentTimeMillis();
    }

    public void setKeysCooldown(UUID uuid) {
        long expireTime = System.currentTimeMillis() + (5L * 60L * 60L * 1000L);
        keysCooldowns.put(uuid, expireTime);
        saveToDatabaseAsync(uuid);
    }

    public boolean hasClaimedOneTime(UUID uuid) {
        return oneTimeClaimed.contains(uuid);
    }

    public void setClaimedOneTime(UUID uuid) {
        oneTimeClaimed.add(uuid);
        saveToDatabaseAsync(uuid);
    }

    // --- OPERACJE NA BAZIE (ASYNC) ---

    public void loadPlayerAsync(UUID uuid) {
        // Używamy nowego, bezpiecznego rekordu FreeItemsData z DatabaseManager
        databaseManager.getPlayerDataAsync(uuid).thenAccept(data -> {
            if (data != null) {
                if (data.trapCooldown() > System.currentTimeMillis()) trapCooldowns.put(uuid, data.trapCooldown());
                if (data.keysCooldown() > System.currentTimeMillis()) keysCooldowns.put(uuid, data.keysCooldown());
                if (data.oneTimeClaimed()) oneTimeClaimed.add(uuid);
            }
        });
    }

    private void saveToDatabaseAsync(UUID uuid) {
        long trap = trapCooldowns.getOrDefault(uuid, 0L);
        long keys = keysCooldowns.getOrDefault(uuid, 0L);
        boolean oneTime = oneTimeClaimed.contains(uuid);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            databaseManager.savePlayerData(uuid, trap, keys, oneTime);
        });
    }

    public void unloadPlayer(UUID uuid) {
        trapCooldowns.remove(uuid);
        keysCooldowns.remove(uuid);
        oneTimeClaimed.remove(uuid);
    }
}
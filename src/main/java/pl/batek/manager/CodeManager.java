package pl.batek.manager;

import org.bukkit.entity.Player;
import pl.batek.database.DatabaseManager;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CodeManager {

    private final DatabaseManager db;
    private final Map<UUID, Set<String>> usedCodesCache = new ConcurrentHashMap<>();

    public CodeManager(DatabaseManager db) {
        this.db = db;
    }

    public void loadPlayer(UUID uuid) {
        db.getUsedCodesAsync(uuid).thenAccept(codes -> {
            usedCodesCache.put(uuid, new HashSet<>(codes));
        });
    }

    public void unloadPlayer(UUID uuid) {
        usedCodesCache.remove(uuid);
    }

    public boolean hasUsedCode(Player player, String codeName) {
        Set<String> codes = usedCodesCache.get(player.getUniqueId());
        return codes != null && codes.contains(codeName.toLowerCase());
    }

    public void markCodeAsUsed(Player player, String codeName) {
        String codeLow = codeName.toLowerCase();
        usedCodesCache.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>()).add(codeLow);
        db.setCodeUsedAsync(player.getUniqueId(), codeLow);
    }
}
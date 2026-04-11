package pl.batek.manager;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import pl.batek.message.MessageType;
import pl.batek.message.PlayerMessage;
import pl.batek.util.SoundsUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeManager {

    private final JavaPlugin plugin;
    private final File file;
    private FileConfiguration config;
    // Mapowanie UUID gracza -> (ID domku 1-5 -> Lokalizacja)
    private final Map<UUID, Map<Integer, Location>> homesCache = new HashMap<>();

    public HomeManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "homes.yml");
        loadHomes();
    }

    public void loadHomes() {
        if (!file.exists()) {
            try { file.createNewFile(); } catch (IOException ignored) {}
        }
        config = YamlConfiguration.loadConfiguration(file);
        homesCache.clear();

        if (config.contains("homes")) {
            for (String uuidStr : config.getConfigurationSection("homes").getKeys(false)) {
                UUID uuid = UUID.fromString(uuidStr);
                Map<Integer, Location> playerHomes = new HashMap<>();
                for (String idStr : config.getConfigurationSection("homes." + uuidStr).getKeys(false)) {
                    int id = Integer.parseInt(idStr);
                    Location loc = config.getLocation("homes." + uuidStr + "." + idStr);
                    if (loc != null) {
                        playerHomes.put(id, loc);
                    }
                }
                homesCache.put(uuid, playerHomes);
            }
        }
    }

    public void saveHomes() {
        config.set("homes", null); // Czyści stary zapis
        for (Map.Entry<UUID, Map<Integer, Location>> entry : homesCache.entrySet()) {
            String uuidStr = entry.getKey().toString();
            for (Map.Entry<Integer, Location> homeEntry : entry.getValue().entrySet()) {
                config.set("homes." + uuidStr + "." + homeEntry.getKey(), homeEntry.getValue());
            }
        }
        try { config.save(file); } catch (IOException ignored) {}
    }

    public Map<Integer, Location> getHomes(UUID uuid) {
        return homesCache.getOrDefault(uuid, new HashMap<>());
    }

    public void setHome(Player player, int id) {
        homesCache.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>()).put(id, player.getLocation());
        saveHomes();
    }

    public void removeHome(Player player, int id) {
        Map<Integer, Location> playerHomes = homesCache.get(player.getUniqueId());
        if (playerHomes != null) {
            playerHomes.remove(id);
            saveHomes();
        }
    }

    public void teleportToHome(Player player, Location location) {
        Location startLoc = player.getLocation().clone();

        new BukkitRunnable() {
            int timeLeft = 5;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }

                if (startLoc.distanceSquared(player.getLocation()) > 1.0) {
                    PlayerMessage.message(player, MessageType.ACTIONBAR, "<red>Teleportacja anulowana!");
                    SoundsUtil.error(player);
                    cancel();
                    return;
                }

                if (timeLeft <= 0) {
                    player.teleport(location);
                    PlayerMessage.message(player, MessageType.ACTIONBAR, "<light_purple>Teleportacja się udała!");
                    SoundsUtil.accept(player);
                    cancel();
                    return;
                }

                PlayerMessage.message(player, MessageType.ACTIONBAR, "<light_purple>Teleportowanie za " + timeLeft + "s");
                SoundsUtil.retro(player);
                timeLeft--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
}
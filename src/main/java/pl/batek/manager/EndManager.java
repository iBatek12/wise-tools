package pl.batek.manager;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class EndManager {

    private final JavaPlugin plugin;
    // Mapa przechowująca lokalizację bloku i przypisane mu zadanie (usuwanie po czasie)
    private final Map<Location, BukkitTask> placedBlocks = new HashMap<>();

    public EndManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void addPlacedBlock(Block block) {
        Location loc = block.getLocation();

        // Usuwanie bloku po 90 sekundach (90 * 20 ticków = 1800)
        BukkitTask task = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (placedBlocks.containsKey(loc)) {
                loc.getBlock().setType(Material.AIR);
                placedBlocks.remove(loc);
            }
        }, 1800L);

        placedBlocks.put(loc, task);
    }

    public boolean isPlayerPlaced(Block block) {
        return placedBlocks.containsKey(block.getLocation());
    }

    public void removePlacedBlock(Block block) {
        // Jeśli gracz zniszczy blok szybciej, anulujemy zadanie, żeby uniknąć błędów
        BukkitTask task = placedBlocks.remove(block.getLocation());
        if (task != null) {
            task.cancel();
        }
    }

    public void clearAll() {
        // Metoda wywoływana przy restarcie serwera
        for (Location loc : placedBlocks.keySet()) {
            loc.getBlock().setType(Material.AIR);
        }
        placedBlocks.clear();
    }
}
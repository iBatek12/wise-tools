package pl.batek.controller;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import pl.batek.config.Configuration;

public class RespawnController implements Listener {

    private final Configuration config;

    public RespawnController(Configuration config) {
        this.config = config;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event) {
        World world = Bukkit.getWorld(config.spawnWorld);
        if (world == null) {
            world = Bukkit.getWorlds().get(0);
        }

        Location spawnLoc = new Location(
                world,
                config.spawnX,
                config.spawnY,
                config.spawnZ,
                config.spawnYaw,
                config.spawnPitch
        );

        // Ustawiamy miejsce odrodzenia gracza
        event.setRespawnLocation(spawnLoc);
    }
}
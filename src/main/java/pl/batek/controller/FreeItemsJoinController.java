package pl.batek.controller;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.batek.manager.FreeItemsManager;

public class FreeItemsJoinController implements Listener {
    private final FreeItemsManager freeItemsManager;

    public FreeItemsJoinController(FreeItemsManager freeItemsManager) {
        this.freeItemsManager = freeItemsManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        freeItemsManager.loadPlayerAsync(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        // Oczyszcza RAM, gdy gracz wychodzi
        freeItemsManager.unloadPlayer(event.getPlayer().getUniqueId());
    }
}
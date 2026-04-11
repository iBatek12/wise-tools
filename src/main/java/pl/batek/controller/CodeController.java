package pl.batek.controller;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.batek.manager.CodeManager;

public class CodeController implements Listener {

    private final CodeManager codeManager;

    public CodeController(CodeManager codeManager) {
        this.codeManager = codeManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        codeManager.loadPlayer(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        codeManager.unloadPlayer(event.getPlayer().getUniqueId());
    }
}
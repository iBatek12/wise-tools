package pl.batek.controller;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.batek.message.MessageType;
import pl.batek.message.PlayerMessage;

public class MechanismController implements Listener {

    // 1. Usuwanie wiadomości o wejściu i wyjściu
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }

    // 2. Blokowanie stawiania bloków mechanizmów (oprócz dźwigni i przycisków)
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Material type = event.getBlock().getType();
        String name = type.name();

        // Pozwalamy na dźwignie (LEVER) i wszystkie rodzaje przycisków (z końcówką _BUTTON)
        if (type == Material.LEVER || name.endsWith("_BUTTON")) {
            return;
        }

        // Lista blokowanych słów kluczowych dla mechanizmów
        if (name.contains("REDSTONE") || name.contains("PISTON") || name.contains("REPEATER") ||
                name.contains("COMPARATOR") || name.contains("OBSERVER") || name.contains("HOPPER") ||
                name.contains("DISPENSER") || name.contains("DROPPER") || name.contains("TNT") ||
                name.contains("TRIPWIRE") || name.contains("TARGET") || name.contains("DAYLIGHT_DETECTOR")) {

            event.setCancelled(true);
            PlayerMessage.message(event.getPlayer(), MessageType.CHAT, "<red>Mechanizmy są zablokowane! Możesz stawiać tylko dźwignie i przyciski.");
        }
    }

    // 3. Całkowite zablokowanie przepływu prądu (Redstone)
    @EventHandler
    public void onRedstoneChange(BlockRedstoneEvent event) {
        // Ustawiamy nową energię na 0, co "mrozi" wszystkie mechanizmy
        event.setNewCurrent(0);
    }
}
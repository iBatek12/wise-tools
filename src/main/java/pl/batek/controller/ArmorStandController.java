package pl.batek.controller;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import pl.batek.config.Configuration;
import pl.batek.message.MessageType;
import pl.batek.message.PlayerMessage;
import pl.batek.util.SoundsUtil;

import java.util.Arrays;

public class ArmorStandController implements Listener {

    private final Configuration config;

    public ArmorStandController(Configuration config) {
        this.config = config;
    }

    @EventHandler
    public void onPlace(PlayerInteractEvent event) {
        // Sprawdzamy, czy gracz klika prawym przyciskiem myszy na blok
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        // Sprawdzamy, czy gracz trzyma w ręce stojak na zbroję
        if (event.getItem() == null || event.getItem().getType() != Material.ARMOR_STAND) return;

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;

        // Pobieramy blok i chunk, w którym gracz próbuje postawić stojak
        Block placedBlock = clickedBlock.getRelative(event.getBlockFace());
        Chunk chunk = placedBlock.getChunk();

        // Liczymy armor standy w tym konkretnym chunku
        long count = Arrays.stream(chunk.getEntities())
                .filter(entity -> entity.getType() == EntityType.ARMOR_STAND)
                .count();

        // Jeśli jest ich więcej lub równo z limitem - anulujemy!
        if (count >= config.armorStandPerChunkLimit) {
            event.setCancelled(true);
            PlayerMessage.message(event.getPlayer(), MessageType.CHAT, config.msgArmorStandLimit);
            SoundsUtil.error(event.getPlayer());

            // Opcjonalnie: odświeżamy ekwipunek, aby stojak nie "zniknął" wizualnie z ręki
            event.getPlayer().updateInventory();
        }
    }
}
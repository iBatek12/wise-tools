package pl.batek.controller;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;
import pl.batek.config.Configuration;
import pl.batek.message.MessageType;
import pl.batek.message.PlayerMessage;
import pl.batek.util.SoundsUtil;

import java.util.concurrent.ThreadLocalRandom;

public class BorderController implements Listener {

    private final Configuration config;

    public BorderController(Configuration config) {
        this.config = config;
    }

    @EventHandler
    public void onRtpInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block clicked = event.getClickedBlock();
        if (clicked == null || !clicked.getType().name().contains("BUTTON")) return;

        // Sprawdzenie czy blok pod guzikiem (lub za nim) to SPONGE
        Block attached = clicked.getRelative(event.getBlockFace().getOppositeFace());
        if (attached.getType() != Material.SPONGE) return;

        Player player = event.getPlayer();

        // Pobieranie świata z konfiguracji (zabezpieczenie, jeśli wpisano złą nazwę)
        World rtpWorld = Bukkit.getWorld(config.rtpWorld);
        if (rtpWorld == null) {
            rtpWorld = player.getWorld();
        }

        // Losowanie kordów z wartości z configu
        int x = ThreadLocalRandom.current().nextInt(config.rtpMin, config.rtpMax + 1);
        int z = ThreadLocalRandom.current().nextInt(config.rtpMin, config.rtpMax + 1);

        // Losowanie znaku (+/-)
        if (ThreadLocalRandom.current().nextBoolean()) x *= -1;
        if (ThreadLocalRandom.current().nextBoolean()) z *= -1;

        Location rtpLoc = new Location(rtpWorld, x, rtpWorld.getHighestBlockYAt(x, z) + 1, z);
        player.teleport(rtpLoc);

        // Zostawiamy pusty TITLE (spacja przed \n) i dajemy pożądany SUBTITLE
        PlayerMessage.message(player, MessageType.SUBTITLE, "<white>Zostałeś <light_purple>przeteleportowany <white>w losowe kordy");
        SoundsUtil.accept(player);
    }

    @EventHandler
    public void onBorderMove(PlayerMoveEvent event) {
        Location to = event.getTo();
        if (to == null) return;

        if (Math.abs(to.getX()) > config.borderSize || Math.abs(to.getZ()) > config.borderSize) {
            Player player = event.getPlayer();

            // POPRAWIONA LINIJKA: Usunięte niepotrzebne .toVector() przy new Vector()
            Vector direction = new Vector(0, to.getY(), 0).subtract(to.toVector()).normalize();
            direction.multiply(1.5).setY(0.5); // Siła odrzutu

            player.setVelocity(direction);
            PlayerMessage.message(player, MessageType.SUBTITLE, "<red>OSIĄGNIĘTO GRANICĘ MAPY!");
            SoundsUtil.error(player);
        }
    }

    @EventHandler
    public void onPearl(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            Location to = event.getTo();
            if (to == null) return;

            // Blokada pereł blisko granicy (np. 5 kratek marginesu z configu)
            if (Math.abs(to.getX()) > config.borderSize - 5 || Math.abs(to.getZ()) > config.borderSize - 5) {
                event.setCancelled(true);
                PlayerMessage.message(event.getPlayer(), MessageType.CHAT, "<red>Nie możesz używać pereł tak blisko granicy mapy!");
                SoundsUtil.error(event.getPlayer());
            }
        }
    }
}
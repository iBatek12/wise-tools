package pl.batek.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import pl.batek.config.Configuration;
import pl.batek.message.MessageType;
import pl.batek.message.PlayerMessage;
import pl.batek.util.SoundsUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpawnManager implements Listener {

    private final JavaPlugin plugin;
    private final Configuration config;
    private final Map<UUID, BukkitTask> teleportTasks = new HashMap<>();

    public SpawnManager(JavaPlugin plugin, Configuration config) {
        this.plugin = plugin;
        this.config = config;
    }

    public void startTeleport(Player player) {
        if (player.hasPermission("core.spawn.bypass")) {
            teleportNow(player);
            return;
        }

        if (teleportTasks.containsKey(player.getUniqueId())) {
            PlayerMessage.message(player, MessageType.CHAT, "<red>Już się teleportujesz na spawn!");
            return;
        }

        BukkitTask task = new BukkitRunnable() {
            int time = 5;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancelTask(player.getUniqueId());
                    return;
                }

                if (time > 0) {
                    PlayerMessage.message(player, MessageType.TITLE_SUBTITLE,
                            "<gradient:#FD6BBA:#F7C6FF><b>TELEPORTACJA</b></gradient>\n<gray>Zostaniesz przeniesiony za <yellow>" + time + " <gray>sekund");
                    SoundsUtil.retro(player);
                    time--;
                } else {
                    teleportNow(player);
                    cancelTask(player.getUniqueId());
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);

        teleportTasks.put(player.getUniqueId(), task);
    }

    private void teleportNow(Player player) {
        World world = Bukkit.getWorld(config.spawnWorld);
        if (world == null) {
            PlayerMessage.message(player, MessageType.CHAT, "<red>Błąd: Świat spawnu nie istnieje! Zgłoś to administracji.");
            return;
        }

        Location spawnLoc = new Location(world, config.spawnX, config.spawnY, config.spawnZ, config.spawnYaw, config.spawnPitch);
        player.teleport(spawnLoc);
        PlayerMessage.message(player, MessageType.TITLE_SUBTITLE, "<green><b>SUKCES</b>\n<gray>Przeteleportowano na spawn!");
        SoundsUtil.accept(player);
    }

    public void cancelTask(UUID uuid) {
        BukkitTask task = teleportTasks.remove(uuid);
        if (task != null) {
            task.cancel();
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!teleportTasks.containsKey(player.getUniqueId())) return;

        Location from = event.getFrom();
        Location to = event.getTo();

        if (to == null) return;

        // Sprawdzamy czy gracz ruszył się o pełny blok (ignorujemy ruch samą kamerą)
        if (from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ()) {
            cancelTask(player.getUniqueId());
            PlayerMessage.message(player, MessageType.TITLE_SUBTITLE, "<red><b>ANULOWANO</b>\n<gray>Poruszyłeś się!");
            PlayerMessage.message(player, MessageType.CHAT, "<red>Teleportacja została anulowana, ponieważ się ruszyłeś!");
            SoundsUtil.error(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        cancelTask(event.getPlayer().getUniqueId());
    }
}
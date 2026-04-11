package pl.batek.manager;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import pl.batek.util.AdventureUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VanishManager implements Listener {
    private final Set<UUID> vanishedPlayers = new HashSet<>();
    private final BossBar vanishBossBar;
    private final JavaPlugin plugin;

    public VanishManager(JavaPlugin plugin) {
        this.plugin = plugin;
        Component bossBarName = AdventureUtil.miniMessage("<red><bold>AKTUALNIE POSIADASZ VANISHA", null);
        this.vanishBossBar = BossBar.bossBar(bossBarName, 1.0f, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
    }

    public boolean toggleVanish(Player player) {
        UUID uuid = player.getUniqueId();
        if (vanishedPlayers.contains(uuid)) {
            vanishedPlayers.remove(uuid);
            player.hideBossBar(this.vanishBossBar);

            for (Player online : Bukkit.getOnlinePlayers()) {
                online.showPlayer(plugin, player);
            }
            return false;
        } else {
            vanishedPlayers.add(uuid);
            player.showBossBar(this.vanishBossBar);

            for (Player online : Bukkit.getOnlinePlayers()) {
                if (!online.hasPermission("wise.vanish.see")) {
                    online.hidePlayer(plugin, player);
                }
            }
            return true;
        }
    }

    public boolean isVanished(Player player) {
        return vanishedPlayers.contains(player.getUniqueId());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player joined = event.getPlayer();
        if (!joined.hasPermission("wise.vanish.see")) {
            for (UUID uuid : vanishedPlayers) {
                Player vanished = Bukkit.getPlayer(uuid);
                if (vanished != null) {
                    joined.hidePlayer(plugin, vanished);
                }
            }
        }

        if (vanishedPlayers.contains(joined.getUniqueId())) {
            joined.showBossBar(this.vanishBossBar);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.getPlayer().hideBossBar(this.vanishBossBar);
    }
}
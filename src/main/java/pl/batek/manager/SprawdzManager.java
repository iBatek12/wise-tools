package pl.batek.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.batek.message.MessageType;
import pl.batek.message.PlayerMessage;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SprawdzManager {
    private final Set<UUID> sprawdzani = new HashSet<>();
    private final JavaPlugin plugin;

    public SprawdzManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.startTitleLoop();
    }

    public void setSprawdzany(UUID uuid, boolean state) {
        if (state) sprawdzani.add(uuid);
        else sprawdzani.remove(uuid);
    }

    public boolean isSprawdzany(UUID uuid) {
        return sprawdzani.contains(uuid);
    }

    private void startTitleLoop() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (UUID uuid : sprawdzani) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null && player.isOnline()) {
                    PlayerMessage.message(player, MessageType.TITLE_SUBTITLE, "<red><bold>JESTEŚ SPRAWDZANY!</bold>\n<gray>Zastosuj się do poleceń administracji na czacie.");
                }
            }
        }, 0L, 40L);
    }
}
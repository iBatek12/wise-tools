package pl.batek.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import pl.batek.config.Configuration;
import pl.batek.message.MessageType;
import pl.batek.message.PlayerMessage;

import java.util.List;

public class AutoMessageManager {
    private final JavaPlugin plugin;
    private final Configuration config;
    private int currentIndex = 0;

    public AutoMessageManager(JavaPlugin plugin, Configuration config) {
        this.plugin = plugin;
        this.config = config;
        this.startTask();
    }

    private void startTask() {
        long intervalTicks = config.autoMessageInterval * 20L;
        new BukkitRunnable() {
            @Override
            public void run() {
                List<String> messages = config.autoMessages;
                if (messages == null || messages.isEmpty()) {
                    return;
                }
                if (Bukkit.getOnlinePlayers().isEmpty()) {
                    return;
                }

                if (currentIndex >= messages.size()) {
                    currentIndex = 0;
                }

                String messageToSend = messages.get(currentIndex);

                for (Player player : Bukkit.getOnlinePlayers()) {
                    PlayerMessage.message(player, MessageType.CHAT, "");
                    PlayerMessage.message(player, MessageType.CHAT, messageToSend);
                    PlayerMessage.message(player, MessageType.CHAT, "");
                }

                currentIndex++;
            }
        }.runTaskTimer(plugin, intervalTicks, intervalTicks);
    }
}
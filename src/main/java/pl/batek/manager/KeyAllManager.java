package pl.batek.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import pl.batek.config.Configuration;
import pl.batek.message.MessageType;
import pl.batek.message.PlayerMessage;
import pl.batek.util.SoundsUtil;

import java.time.LocalTime;
import java.time.ZoneId;

public class KeyAllManager {

    private final JavaPlugin plugin;
    private final Configuration config;
    private final SettingsManager settingsManager;

    private int lastAwardedHour = -1;
    private int lastAnnouncedSecond = -1;

    public KeyAllManager(JavaPlugin plugin, Configuration config, SettingsManager settingsManager) {
        this.plugin = plugin;
        this.config = config;
        this.settingsManager = settingsManager;
        this.startKeyAllTask();
    }

    private void startKeyAllTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                LocalTime now = LocalTime.now(ZoneId.of("Europe/Warsaw"));
                int hour = now.getHour();
                int minute = now.getMinute();
                int second = now.getSecond();

                // 1. ODLICZANIE
                if (minute == 59 && second >= 45) {
                    int nextHour = (hour + 1) % 24;

                    if (nextHour >= config.keyallStartHour && nextHour <= config.keyallEndHour) {
                        if (second != lastAnnouncedSecond) {
                            lastAnnouncedSecond = second;
                            int secondsLeft = 60 - second;

                            for (Player player : Bukkit.getOnlinePlayers()) {
                                // Sprawdzamy, czy gracz ma włączone powiadomienia w /ustawienia
                                if (settingsManager.isTitleVisible(player.getUniqueId())) {
                                    PlayerMessage.message(player, MessageType.TITLE_SUBTITLE,
                                            "<red><b>KEYALL</b>\n<gray>Za <white>" + secondsLeft + " <gray>zostaną rozdane klucze!");

                                    // Dźwięk przeniesiony tutaj, wywoła się tylko jeśli gracz ma włączone powiadomienia
                                    SoundsUtil.retro(player);
                                }
                            }
                        }
                    }
                }

                // 2. ROZDANIE NAGRÓD
                if (minute == 0 && hour != lastAwardedHour) {
                    if (hour >= config.keyallStartHour && hour <= config.keyallEndHour) {
                        lastAwardedHour = hour;

                        for (String cmd : config.keyallCommands) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                        }

                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (settingsManager.isTitleVisible(player.getUniqueId())) {
                                PlayerMessage.message(player, MessageType.TITLE_SUBTITLE,
                                        "<red><b>KEYALL</b>\n<green>Klucze zostały rozdane!");

                                // Dźwięk przeniesiony tutaj, wywoła się tylko jeśli gracz ma włączone powiadomienia
                                SoundsUtil.celebrate(player);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }
}
package pl.batek.manager;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import pl.batek.config.Configuration;
import pl.batek.util.AdventureUtil;
import pl.batek.util.FormatUtil;
import pl.batek.util.SoundsUtil;

import java.util.HashMap;
import java.util.Map;

public class EventManager implements Listener {

    private final JavaPlugin plugin;
    private final Configuration config;

    // Przechowujemy zadania i paski BossBar przypisane do konkretnego TYPU eventu (np. "smok", "keyall")
    private final Map<String, BukkitTask> activeEvents = new HashMap<>();
    private final Map<String, BossBar> activeBossBars = new HashMap<>();

    public EventManager(JavaPlugin plugin, Configuration config) {
        this.plugin = plugin;
        this.config = config;
    }

    public void startEvent(String type, int seconds, String keyType, int amount) {
        // Zatrzymujemy tylko event tego samego typu, żeby nie zepsuć innych odliczań!
        stopEvent(type);

        String displayName = "Event";
        BarColor barColor = config.keyAllBossBarColor;
        BarStyle barStyle = config.keyAllBossBarStyle;

        // Inicjalizacja danych dla konkretnych eventów
        if (type.equalsIgnoreCase("keyall") && keyType != null) {
            String configData = config.keyAllKeys.get(keyType);
            if (configData != null && configData.contains("|")) {
                displayName = configData.split("\\|")[0];
            }
        } else if (type.equalsIgnoreCase("smok")) {
            displayName = "Smok";
            barColor = BarColor.PURPLE; // Zmieniony kolor paska dla smoka
        }

        // Tworzymy NOWY BossBar dla tego konkretnego eventu
        BossBar bossBar = Bukkit.createBossBar("", barColor, barStyle);
        for (Player p : Bukkit.getOnlinePlayers()) {
            bossBar.addPlayer(p);
        }

        // Zapisujemy go w pamięci pod kluczem typu eventu
        activeBossBars.put(type.toLowerCase(), bossBar);

        final String finalDisplayName = displayName;

        BukkitTask task = new BukkitRunnable() {
            int timeLeft = seconds;
            final int totalTime = seconds;

            @Override
            public void run() {
                if (timeLeft <= 0) {
                    executeEvent(type, keyType, amount, finalDisplayName);
                    stopEvent(type);
                    return;
                }

                // Aktualizujemy konkretny BossBar
                String timeStr = FormatUtil.formatTime(timeLeft);
                String rawTitle;

                if (type.equalsIgnoreCase("keyall")) {
                    rawTitle = config.keyAllBossBarTitle
                            .replace("{TIME}", timeStr)
                            .replace("{KEY}", finalDisplayName)
                            .replace("{AMOUNT}", String.valueOf(amount));
                } else {
                    rawTitle = "&#FF00FF☠ &8⁑ &fZa &#FF00FF{TIME} &fna spawnie pojawi się &d&lSMOK&f!"
                            .replace("{TIME}", timeStr);
                }

                Component component = AdventureUtil.translate(rawTitle);
                String bukkitTitle = LegacyComponentSerializer.legacySection().serialize(component);

                bossBar.setTitle(bukkitTitle);
                bossBar.setProgress(Math.max(0.0, Math.min(1.0, (double) timeLeft / totalTime)));

                // Dźwięk retro w ostatnich 5 sekundach
                if (timeLeft <= 5) {
                    for (Player p : Bukkit.getOnlinePlayers()) SoundsUtil.retro(p);
                }

                timeLeft--;
            }
        }.runTaskTimer(plugin, 0L, 20L);

        activeEvents.put(type.toLowerCase(), task);
    }

    public void stopEvent(String type) {
        String lowerType = type.toLowerCase();

        // Anulujemy zadanie odliczania
        BukkitTask task = activeEvents.remove(lowerType);
        if (task != null) task.cancel();

        // Usuwamy i chowamy BossBar tylko dla TEGO wybranego eventu
        BossBar bossBar = activeBossBars.remove(lowerType);
        if (bossBar != null) {
            bossBar.removeAll();
        }
    }

    private void executeEvent(String type, String keyType, int amount, String displayName) {
        if (type.equalsIgnoreCase("keyall") && keyType != null) {
            String configData = config.keyAllKeys.get(keyType);
            if (configData != null && configData.contains("|")) {
                String command = configData.split("\\|")[1].replace("{AMOUNT}", String.valueOf(amount));
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }
            Bukkit.broadcast(AdventureUtil.translate("&a&lKEYALL &8» &fCały serwer otrzymał &e" + amount + "x " + displayName + "&f!"));
        }
        else if (type.equalsIgnoreCase("smok")) {
            World world = Bukkit.getWorld(config.spawnWorld);
            if (world != null) {
                Location loc = new Location(world, config.spawnX, config.spawnY + 20, config.spawnZ);
                world.spawnEntity(loc, EntityType.ENDER_DRAGON);
                Bukkit.broadcast(AdventureUtil.translate("&d&lEVENT &8» &fSmok odrodził się na spawnie! &cWalczcie!"));
            }
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            SoundsUtil.celebrate(p);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        for (BossBar bossBar : activeBossBars.values()) {
            bossBar.addPlayer(event.getPlayer());
        }
    }
}
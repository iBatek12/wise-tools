package pl.batek.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import pl.batek.database.DatabaseManager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlaytimeManager implements Listener {
    private final JavaPlugin plugin;
    private final DatabaseManager db;
    private final Map<UUID, Long> joinTimes = new ConcurrentHashMap<>();
    private final Map<UUID, Long> cachedPlaytime = new ConcurrentHashMap<>();
    private final Map<UUID, Long> cachedTimeCoins = new ConcurrentHashMap<>();
    private final Map<UUID, Long> pendingCoinSeconds = new ConcurrentHashMap<>(); // Przetrzymuje sekundy do pełnych 5 minut

    public PlaytimeManager(JavaPlugin plugin, DatabaseManager db) {
        this.plugin = plugin;
        this.db = db;
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::saveAllOnline, 20L * 60, 20L * 60);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        joinTimes.put(uuid, System.currentTimeMillis());
        pendingCoinSeconds.put(uuid, 0L);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            long[] stats = db.getPlaytimeAndCoinsSync(uuid);
            cachedPlaytime.put(uuid, stats[0]);
            cachedTimeCoins.put(uuid, stats[1]);
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        // Zabezpieczenie przed utratą danych - pobieramy zmienne PRZED ich usunięciem!
        Long joinTime = joinTimes.remove(uuid);
        Long currentTotal = cachedPlaytime.remove(uuid);
        Long currentCoins = cachedTimeCoins.remove(uuid);
        Long pending = pendingCoinSeconds.remove(uuid);

        if (joinTime != null && currentTotal != null && currentCoins != null) {
            long now = System.currentTimeMillis();
            long sessionSeconds = (now - joinTime) / 1000;

            long finalTotal = currentTotal + sessionSeconds;

            // Obliczamy ile monet zarobił gracz przy wyjściu (1 moneta za 300s = 5 minut)
            long totalPending = (pending != null ? pending : 0L) + sessionSeconds;
            long coinsEarned = totalPending / 300;
            long finalCoins = currentCoins + coinsEarned;

            // Zapisujemy policzone dane asynchronicznie do bazy
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                db.savePlaytimeSync(uuid, finalTotal, finalCoins);
            });
        }
    }

    public long getTimeCoins(UUID uuid) {
        return cachedTimeCoins.getOrDefault(uuid, 0L);
    }

    public void removeTimeCoins(UUID uuid, long amount) {
        long current = getTimeCoins(uuid);
        cachedTimeCoins.put(uuid, Math.max(0, current - amount));
    }

    private void saveAllOnline() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            savePlayerSync(player.getUniqueId());
        }
    }

    private void savePlayerSync(UUID uuid) {
        Long joinTime = joinTimes.get(uuid);
        Long currentTotal = cachedPlaytime.getOrDefault(uuid, 0L);
        Long currentCoins = cachedTimeCoins.getOrDefault(uuid, 0L);
        Long pending = pendingCoinSeconds.getOrDefault(uuid, 0L);

        if (joinTime != null) {
            long now = System.currentTimeMillis();
            long sessionSeconds = (now - joinTime) / 1000;

            joinTimes.put(uuid, now); // Resetujemy czas dołączenia do "teraz"
            currentTotal += sessionSeconds;

            // Obliczamy zarobione monety: 1 moneta za równe 300 sek
            long totalPending = pending + sessionSeconds;
            long coinsEarned = totalPending / 300;
            long leftover = totalPending % 300; // Zachowujemy "resztę" sekund na kolejny zapis

            pendingCoinSeconds.put(uuid, leftover);
            currentCoins += coinsEarned;

            cachedPlaytime.put(uuid, currentTotal);
            cachedTimeCoins.put(uuid, currentCoins);

            db.savePlaytimeSync(uuid, currentTotal, currentCoins);
        }
    }
}
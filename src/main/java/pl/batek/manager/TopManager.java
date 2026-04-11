package pl.batek.manager;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import pl.batek.database.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

public class TopManager {
    private final DatabaseManager db;

    public record TopPlayer(String name, double value) {}

    private final List<TopPlayer> topMoney = new ArrayList<>();
    private final List<TopPlayer> topTime = new ArrayList<>();

    public TopManager(JavaPlugin plugin, DatabaseManager db) {
        this.db = db;
        // Pobiera topki z MySQL co 5 minut (6000 ticków) – totalnie asynchronicznie!
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::refreshTops, 20L, 6000L);
    }

    private void refreshTops() {
        List<TopPlayer> newTopMoney = new ArrayList<>();
        for (DatabaseManager.TopEntry entry : db.getTopEconomySync()) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(entry.uuid());
            String name = op.getName() != null ? op.getName() : "Brak";
            newTopMoney.add(new TopPlayer(name, entry.value()));
        }

        List<TopPlayer> newTopTime = new ArrayList<>();
        for (DatabaseManager.TopEntry entry : db.getTopTimeSync()) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(entry.uuid());
            String name = op.getName() != null ? op.getName() : "Brak";
            newTopTime.add(new TopPlayer(name, entry.value()));
        }

        // Zapis do bezpiecznej zmiennej
        synchronized (this) {
            topMoney.clear();
            topMoney.addAll(newTopMoney);

            topTime.clear();
            topTime.addAll(newTopTime);
        }
    }

    public synchronized TopPlayer getTopMoney(int position) {
        if (position < 1 || position > topMoney.size()) return new TopPlayer("Brak", 0);
        return topMoney.get(position - 1);
    }

    public synchronized TopPlayer getTopTime(int position) {
        if (position < 1 || position > topTime.size()) return new TopPlayer("Brak", 0);
        return topTime.get(position - 1);
    }
}
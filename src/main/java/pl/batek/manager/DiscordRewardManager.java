package pl.batek.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.batek.config.Configuration;
import pl.batek.database.DatabaseManager;
import pl.batek.message.MessageType;
import pl.batek.message.PlayerMessage;
import pl.batek.util.SoundsUtil;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DiscordRewardManager {
    private final JavaPlugin plugin;
    private final Configuration config;
    private final DatabaseManager db;

    // Rozdzielone sety: jeden sprawdza id discorda, drugi sprawdza nicki
    private final Set<String> claimedDiscordIds = ConcurrentHashMap.newKeySet();
    private final Set<String> claimedNicks = ConcurrentHashMap.newKeySet();

    public DiscordRewardManager(JavaPlugin plugin, Configuration config, DatabaseManager db) {
        this.plugin = plugin;
        this.config = config;
        this.db = db;
        loadData();
    }

    private void loadData() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            db.loadDiscordRewardsSync(claimedDiscordIds, claimedNicks);
        });
    }

    // Sprawdza czy dany nick dostał już nagrodę (potrzebne do GUI)
    public boolean hasClaimed(String playerName) {
        return claimedNicks.contains(playerName.toLowerCase());
    }

    // Zabezpieczenie przed podaniem konta discord
    public boolean hasClaimedDiscord(String discordId) {
        return claimedDiscordIds.contains(discordId);
    }

    public void claimReward(String discordId, String playerName) {
        String nickLower = playerName.toLowerCase();

        if (claimedNicks.contains(nickLower) || claimedDiscordIds.contains(discordId)) return;

        // Dodajemy do obu cache
        claimedNicks.add(nickLower);
        claimedDiscordIds.add(discordId);

        // Zapisujemy w bazie
        db.setDiscordRewardClaimedAsync(discordId, nickLower);

        Bukkit.getScheduler().runTask(plugin, () -> {
            String cmd = config.discordRewardCommand.replace("{player}", playerName);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);

            Player player = Bukkit.getPlayerExact(playerName);
            if (player != null && player.isOnline()) {
                SoundsUtil.itemshop(player);
            }

            String broadcastMsg = String.join("<newline>",
                    " ",
                    "<#6FC1F9>☀ <dark_gray>⁑ <white>Gracz <#97CFF5>" + playerName + " <white>odebrał range <#FFFF9E><bold>ᴠɪᴘ</bold>",
                    "<#6FC1F9>☀ <dark_gray>⁑ <white>Nie bądź gorszy i też odbierz go pod <#97CFF5><underlined>/discord</underlined>",
                    " "
            );

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                PlayerMessage.message(onlinePlayer, MessageType.CHAT, broadcastMsg);
            }
        });
    }
}
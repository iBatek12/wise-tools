package pl.batek.manager;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import pl.batek.config.Configuration;
import pl.batek.economy.EconomyManager;
import pl.batek.message.MessageType;
import pl.batek.message.PlayerMessage;
import pl.batek.util.AdventureUtil;
import pl.batek.util.FormatUtil;
import pl.batek.util.SoundsUtil;

import java.time.Duration;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AfkZoneManager implements Runnable, Listener {
    private static class AfkState {

        Location lastLocation;
        int idleSeconds = 0;
        boolean countingDown = false;
        boolean inAfkZone = false;
        int stdSeconds = 0;
        int preSeconds = 0;
        BossBar stdBar;
        BossBar preBar;
    }

    private final Map<UUID, AfkState> states = new ConcurrentHashMap<>();
    private final Plugin plugin;
    private final Configuration config;
    private final EconomyManager economyManager;
    private final Random random = new Random();

    public AfkZoneManager(Plugin plugin, Configuration config, EconomyManager economyManager) {
        this.plugin = plugin;
        this.config = config;
        this.economyManager = economyManager;
        Bukkit.getScheduler().runTaskTimer(plugin, this, 20L, 20L); // Uruchamiane co sekundę
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            AfkState state = states.computeIfAbsent(player.getUniqueId(), k -> new AfkState());
            Location current = player.getLocation();

            boolean inSpawn = false;
            boolean inAfkZone = false;

            try {
                com.sk89q.worldedit.util.Location weLoc = BukkitAdapter.adapt(current);
                ApplicableRegionSet set = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().getApplicableRegions(weLoc);
                for (ProtectedRegion region : set) {
                    if (region.getId().equalsIgnoreCase("spawn")) inSpawn = true;
                    if (region.getId().equalsIgnoreCase(config.afkZoneRegion)) inAfkZone = true;
                }
            } catch (Exception ignored) {
            }

            // --- 1. SYSTEM TELEPORTACJI ZE SPAWNA PO BEZRUCHU ---
            if (state.lastLocation != null && state.lastLocation.getWorld().equals(current.getWorld()) && state.lastLocation.distanceSquared(current) < 0.05) {
                if (inSpawn) {
                    state.idleSeconds++;

                    if (state.idleSeconds >= 300) { // 5 minut
                        state.countingDown = true;
                        int remaining = 310 - state.idleSeconds;

                        if (remaining > 0) {
                            sendAfkTitle(player, remaining);
                            SoundsUtil.arrow(player);
                        } else {
                            teleportToAfk(player);
                            resetIdleState(player, state);
                        }
                    }
                } else {
                    resetIdleState(player, state);
                }
            } else {
                state.lastLocation = current;
                resetIdleState(player, state);
            }

            // --- 2. SYSTEM NAGRÓD W STREFIE AFK ---
            if (!config.afkZoneEnabled) continue;

            if (inAfkZone) {
                if (!state.inAfkZone) {
                    state.inAfkZone = true;
                    initRewardState(player, state);
                }
                processRewards(player, state);
            } else {
                if (state.inAfkZone) {
                    state.inAfkZone = false;
                    clearRewardState(player, state);
                }
            }
        }
    }

    // --- METODY DO TELEPORTACJI ---

    private void resetIdleState(Player player, AfkState state) {
        if (state.countingDown) {
            player.clearTitle();
        }
        state.idleSeconds = 0;
        state.countingDown = false;
    }

    private void sendAfkTitle(Player player, int seconds) {
        Component title = AdventureUtil.translate("&c&lSYSTEM AFK");
        Component subtitle = AdventureUtil.translate("&7Za &e" + seconds + " &7zostaniesz przeteleportowany");
        Title.Times times = Title.Times.times(Duration.ZERO, Duration.ofSeconds(2), Duration.ZERO);
        player.showTitle(Title.title(title, subtitle, times));
    }

    private void teleportToAfk(Player player) {
        org.bukkit.World world = Bukkit.getWorld(config.afkWorld);
        if (world == null) world = player.getWorld();

        Location afkLoc = new Location(world, config.afkX, config.afkY, config.afkZ, config.afkYaw, config.afkPitch);

        player.teleportAsync(afkLoc).thenAccept(success -> {
            if (success) {
                player.sendMessage(AdventureUtil.translate("&8>> &7Zostałeś przeniesiony na strefę &cAFK&7."));
                SoundsUtil.accept(player);
            }
        });
    }

    private void initRewardState(Player player, AfkState state) {
        PlayerMessage.message(player, MessageType.ACTIONBAR, config.msgAfkEnabled);
        state.stdSeconds = 0;
        state.preSeconds = 0;

        String stdStyleStr = config.afkStandardBossBarStyle.toUpperCase().replace("SEGMENTED", "NOTCHED");
        String preStyleStr = config.afkPremiumBossBarStyle.toUpperCase().replace("SEGMENTED", "NOTCHED");

        BossBar.Color stdColor = BossBar.Color.valueOf(config.afkStandardBossBarColor.toUpperCase());
        BossBar.Overlay stdOverlay = BossBar.Overlay.valueOf(stdStyleStr);

        BossBar.Color preColor = BossBar.Color.valueOf(config.afkPremiumBossBarColor.toUpperCase());
        BossBar.Overlay preOverlay = BossBar.Overlay.valueOf(preStyleStr);

        state.stdBar = BossBar.bossBar(Component.empty(), 0f, stdColor, stdOverlay);
        state.preBar = BossBar.bossBar(Component.empty(), 0f, preColor, preOverlay);

        player.showBossBar(state.stdBar);
        player.showBossBar(state.preBar);
    }

    private void processRewards(Player player, AfkState state) {
        state.stdSeconds++;
        if (state.stdSeconds >= config.afkStandardRewardSeconds) {
            double currentBalance = economyManager.getBalance(player);
            economyManager.setBalance(player, currentBalance + config.afkStandardRewardMoney);
            state.stdSeconds = 0;
        }
        updateBar(state.stdBar, config.afkStandardRewardTitle, state.stdSeconds, config.afkStandardRewardSeconds, config.afkStandardRewardMoney, 0);

        // Nagroda premium (Klucz)
        state.preSeconds++;
        if (state.preSeconds >= config.afkPremiumRewardSeconds) {
            givePremiumReward(player);
            state.preSeconds = 0;
        }
        updateBar(state.preBar, config.afkPremiumRewardTitle, state.preSeconds, config.afkPremiumRewardSeconds, 0, getChance(player));
    }

    private void updateBar(BossBar bar, String rawTitle, int current, int max, double money, double chance) {
        if (bar == null) return;

        float progress = (float) current / max;
        bar.progress(Math.min(Math.max(progress, 0f), 1f));
        String timeStr = FormatUtil.formatTime(max - current);
        String percent = String.valueOf((int) (progress * 100));
        Map<String, String> placeholders = Map.of(
                "time", timeStr,
                "percentage", percent,
                "money", String.valueOf(money),
                "chance", String.valueOf(chance)
        );

        Component titleComp = AdventureUtil.miniMessage(rawTitle, placeholders);
        bar.name(titleComp);
    }

    private void givePremiumReward(Player player) {
        double chance = getChance(player);
        double roll = random.nextDouble() * 100.0;

        if (roll <= chance) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), config.afkPremiumRewardCommand.replace("<player>", player.getName()));
            PlayerMessage.message(player, MessageType.CHAT, config.afkPremiumRewardSuccessMessage);
            SoundsUtil.celebrate(player);
        } else {
            PlayerMessage.message(player, MessageType.ACTIONBAR, config.afkPremiumRewardFailMessage);
            SoundsUtil.error(player);
        }
    }

    private double getChance(Player player) {
        if (player.hasPermission("core.gvip")) return 90.0;
        if (player.hasPermission("core.mvip")) return 80.0;
        if (player.hasPermission("core.svip")) return 70.0;
        if (player.hasPermission("core.vip")) return 60.0;
        if (player.hasPermission("core.gracz")) return 40.0;
        return 0.0;
    }

    private void clearRewardState(Player player, AfkState state) {
        PlayerMessage.message(player, MessageType.ACTIONBAR, config.msgAfkDisabled);
        if (state.stdBar != null) {
            player.hideBossBar(state.stdBar);
            state.stdBar = null;
        }
        if (state.preBar != null) {
            player.hideBossBar(state.preBar);
            state.preBar = null;
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        AfkState state = states.remove(player.getUniqueId());

        if (state != null) {
            if (state.stdBar != null) player.hideBossBar(state.stdBar);
            if (state.preBar != null) player.hideBossBar(state.preBar);
        }
    }
}
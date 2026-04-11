package pl.batek.command.commands.teleport;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import pl.batek.WiseMain;
import pl.batek.manager.TpaManager;
import pl.batek.message.MessageType;
import pl.batek.message.PlayerMessage;
import pl.batek.util.AdventureUtil;
import pl.batek.util.SoundsUtil;

import java.util.List;
import java.util.UUID;

@Command(name = "tpaccept", aliases = {"tpaakceptuj"})
public class TpacceptCommand {

    private final TpaManager tpaManager;

    public TpacceptCommand(TpaManager tpaManager) {
        this.tpaManager = tpaManager;
    }

    // Wariant 1: /tpaccept (bez argumentów)
    @Execute
    void executeDefault(@Context Player target) {
        List<UUID> validRequests = tpaManager.getValidRequests(target);

        if (validRequests.isEmpty()) {
            target.sendMessage(AdventureUtil.translate("&cNie masz żadnych oczekujących próśb o teleportację!"));
            SoundsUtil.error(target);
            return;
        }

        // Pobieramy ostatnią prośbę z listy (najnowszą)
        UUID requesterUUID = validRequests.get(validRequests.size() - 1);
        Player requester = Bukkit.getPlayer(requesterUUID);

        if (requester == null || !requester.isOnline()) {
            target.sendMessage(AdventureUtil.translate("&cGracz jest już offline!"));
            tpaManager.clearRequests(target);
            return;
        }

        tpaManager.removeRequest(target, requester);
        acceptSingle(target, requester);
    }

    // Wariant 2: /tpaccept [nick] lub /tpaccept *
    @Execute
    void executeArg(@Context Player target, @Arg("nick/*") String arg) {
        List<UUID> validRequests = tpaManager.getValidRequests(target);

        if (validRequests.isEmpty()) {
            target.sendMessage(AdventureUtil.translate("&cNie masz żadnych oczekujących próśb o teleportację!"));
            SoundsUtil.error(target);
            return;
        }

        // --- OPCJA: WSZYSCY (*) ---
        if (arg.equals("*")) {
            int count = 0;
            for (UUID uuid : validRequests) {
                Player requester = Bukkit.getPlayer(uuid);
                if (requester != null && requester.isOnline()) {
                    acceptSingle(target, requester);
                    count++;
                }
            }
            tpaManager.clearRequests(target);
            if (count > 0) {
                target.sendMessage(AdventureUtil.translate("&aZaakceptowano wszystkie prośby (&7" + count + "&a)!"));
            }
            return;
        }

        // --- OPCJA: KONKRETNY NICK ---
        Player requester = Bukkit.getPlayer(arg);
        if (requester == null || !requester.isOnline()) {
            target.sendMessage(AdventureUtil.translate("&cGracz &7" + arg + " &cjest offline!"));
            return;
        }

        if (!validRequests.contains(requester.getUniqueId())) {
            target.sendMessage(AdventureUtil.translate("&cTen gracz nie wysłał Ci prośby!"));
            return;
        }

        tpaManager.removeRequest(target, requester);
        acceptSingle(target, requester);
    }

    private void acceptSingle(Player target, Player requester) {
        target.sendMessage(AdventureUtil.translate("&aZaakceptowano prośbę od &7" + requester.getName()));
        requester.sendMessage(AdventureUtil.translate("&aGracz &7" + target.getName() + " &azaakceptował prośbę! Teleportacja nastąpi za 5s..."));

        startTeleportProcedure(requester, target);
    }

    private void startTeleportProcedure(Player requester, Player target) {
        // Efekty Blindness i Slowness
        requester.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 140, 1, false, false, false));

        PotionEffectType slownessType = PotionEffectType.getByName("SLOWNESS") != null ?
                PotionEffectType.getByName("SLOWNESS") : PotionEffectType.getByName("SLOW");

        if (slownessType != null) {
            requester.addPotionEffect(new PotionEffect(slownessType, 140, 4, false, false, false));
        }

        Location startLoc = requester.getLocation().clone();

        new BukkitRunnable() {
            int timeLeft = 5;

            @Override
            public void run() {
                if (!requester.isOnline() || !target.isOnline()) {
                    this.cancel();
                    return;
                }

                // Anulowanie przy ruchu (tolerancja 1 kratka)
                if (startLoc.distanceSquared(requester.getLocation()) > 1.0) {
                    PlayerMessage.message(requester, MessageType.TITLE_SUBTITLE, "<red>ANULOWANO\n<gray>Poruszyłeś się!");
                    removeEffects(requester, slownessType);
                    this.cancel();
                    return;
                }

                if (timeLeft <= 0) {
                    requester.teleport(target.getLocation());
                    PlayerMessage.message(requester, MessageType.TITLE_SUBTITLE, "<green>SUKCES\n<gray>Przetransportowano!");
                    SoundsUtil.accept(requester);
                    removeEffects(requester, slownessType);
                    this.cancel();
                    return;
                }

                PlayerMessage.message(requester, MessageType.TITLE_SUBTITLE, "<gold>TELEPORTACJA\n<gray>Nie ruszaj się! <white>" + timeLeft + "s");
                SoundsUtil.retro(requester);
                timeLeft--;
            }
        }.runTaskTimer(WiseMain.getInstance(), 0L, 20L);
    }

    private void removeEffects(Player player, PotionEffectType slownessType) {
        player.removePotionEffect(PotionEffectType.BLINDNESS);
        if (slownessType != null) player.removePotionEffect(slownessType);
    }
}
package pl.batek.command.commands.tools;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.batek.config.Configuration;
import pl.batek.manager.CodeManager;
import pl.batek.message.MessageType;
import pl.batek.message.PlayerMessage;
import pl.batek.util.AdventureUtil;
import pl.batek.util.SoundsUtil;

@Command(name = "kod")
public class CodeCommand {

    private final Configuration config;
    private final CodeManager codeManager;
    private final JavaPlugin plugin;

    public CodeCommand(Configuration config, CodeManager codeManager, JavaPlugin plugin) {
        this.config = config;
        this.codeManager = codeManager;
        this.plugin = plugin;
    }

    @Execute
    void execute(@Context Player player, @Arg("nazwa") String codeName) {
        // Wyszukiwanie kodu w configu
        Configuration.CodeReward reward = config.codes.stream()
                .filter(c -> c.name.equalsIgnoreCase(codeName))
                .findFirst()
                .orElse(null);

        if (reward == null) {
            PlayerMessage.message(player, MessageType.CHAT, "<red>Taki kod nie istnieje!");
            SoundsUtil.error(player);
            return;
        }

        if (codeManager.hasUsedCode(player, reward.name)) {
            PlayerMessage.message(player, MessageType.CHAT, "<red>Już użyłeś tego kodu!");
            SoundsUtil.error(player);
            return;
        }

        long requiredTicks = parseTimeToTicks(reward.requiredTime);
        long playedTicks = player.getStatistic(Statistic.PLAY_ONE_MINUTE);

        if (playedTicks < requiredTicks) {
            PlayerMessage.message(player, MessageType.CHAT, "<red>Musisz przegrać co najmniej <yellow>" + reward.requiredTime + " <red>aby użyć tego kodu!");
            SoundsUtil.error(player);
            return;
        }

        // Zaznacz jako użyty (zapobiega to "spamowaniu" komendy przed zapisem bazy)
        codeManager.markCodeAsUsed(player, reward.name);

        // Wykonywanie komend w głównym wątku
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (String cmd : reward.commands) {
                String finalCmd = cmd.replace("<player>", player.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCmd);
            }
        });

        // Broadcast wiadomości
        for (String line : reward.broadcast) {
            if (line.isEmpty()) {
                Bukkit.broadcast(Component.empty());
                continue;
            }
            Component comp = AdventureUtil.miniMessage(line.replace("<player>", player.getName()), null);
            Bukkit.broadcast(comp);
        }

        SoundsUtil.itemshop(player);
    }

    private long parseTimeToTicks(String timeString) {
        if (timeString == null || timeString.isEmpty()) return 0;
        timeString = timeString.toLowerCase();
        int multiplier = 20; // Domyślnie sekundy (1 sekunda = 20 ticków)

        if (timeString.endsWith("m")) {
            multiplier = 20 * 60;
            timeString = timeString.replace("m", "");
        } else if (timeString.endsWith("h")) {
            multiplier = 20 * 60 * 60;
            timeString = timeString.replace("h", "");
        } else if (timeString.endsWith("s")) {
            multiplier = 20;
            timeString = timeString.replace("s", "");
        }

        try {
            return Long.parseLong(timeString) * multiplier;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
package pl.batek.command.commands.tools;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.batek.message.MessageType;
import pl.batek.message.PlayerMessage;
import pl.batek.util.SoundsUtil;

import java.util.Map;

@Command(name = "eco")
@Permission("admin.eco")
public class EcoCommand {

    private final Economy economy;

    public EcoCommand(Economy economy) {
        this.economy = economy;
    }

    @Execute(name = "add")
    void add(@Context CommandSender sender, @Arg("gracz") OfflinePlayer target, @Arg("kwota") double amount) {
        economy.depositPlayer(target, amount);
        PlayerMessage.message(sender, MessageType.CHAT, "<green>Dodano <yellow>{amount}<green> do konta <white>{player}",
                Map.of("amount", economy.format(amount), "player", target.getName() != null ? target.getName() : "Nieznany"));
        if (sender instanceof Player) SoundsUtil.accept((Player) sender);
    }

    @Execute(name = "take")
    void take(@Context CommandSender sender, @Arg("gracz") OfflinePlayer target, @Arg("kwota") double amount) {
        economy.withdrawPlayer(target, amount);
        PlayerMessage.message(sender, MessageType.CHAT, "<red>Zabrano <yellow>{amount}<red> z konta <white>{player}",
                Map.of("amount", economy.format(amount), "player", target.getName() != null ? target.getName() : "Nieznany"));
        if (sender instanceof Player) SoundsUtil.glass((Player) sender);
    }

    @Execute(name = "set")
    void set(@Context CommandSender sender, @Arg("gracz") OfflinePlayer target, @Arg("kwota") double amount) {
        double current = economy.getBalance(target);
        economy.withdrawPlayer(target, current);
        economy.depositPlayer(target, amount);
        PlayerMessage.message(sender, MessageType.CHAT, "<green>Ustawiono stan konta <white>{player} <green>na <yellow>{amount}",
                Map.of("amount", economy.format(amount), "player", target.getName() != null ? target.getName() : "Nieznany"));
        if (sender instanceof Player) SoundsUtil.accept((Player) sender);
    }
}
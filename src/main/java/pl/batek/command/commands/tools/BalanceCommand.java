package pl.batek.command.commands.tools;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.batek.message.MessageType;
import pl.batek.message.PlayerMessage;

import java.util.Map;

@Command(name = "balance", aliases = {"bal", "money"})
public class BalanceCommand {

    private final Economy economy;

    public BalanceCommand(Economy economy) {
        this.economy = economy;
    }

    // Sprawdzanie własnego stanu konta (Tylko dla graczy)
    @Execute
    void checkSelf(@Context Player player) {
        double balance = economy.getBalance(player);
        PlayerMessage.message(player, MessageType.CHAT, "<green>Twój stan konta wynosi: <yellow>{amount}",
                Map.of("amount", economy.format(balance)));
    }

    // Sprawdzanie stanu konta innego gracza (Dla graczy i konsoli)
    @Execute
    void checkOther(@Context CommandSender sender, @Arg("gracz") OfflinePlayer target) {
        double balance = economy.getBalance(target);
        String targetName = target.getName() != null ? target.getName() : "Nieznany";

        PlayerMessage.message(sender, MessageType.CHAT, "<green>Stan konta gracza <white>{player} <green>wynosi: <yellow>{amount}",
                Map.of(
                        "amount", economy.format(balance),
                        "player", targetName
                ));
    }
}
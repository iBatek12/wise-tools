package pl.batek.command.commands.tools;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context; // DODANY IMPORT
import dev.rollczi.litecommands.annotations.execute.Execute;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import pl.batek.message.MessageType;
import pl.batek.message.PlayerMessage;
import pl.batek.util.SoundsUtil;

import java.util.Map;

@Command(name = "pay")
public class PayCommand {

    private final Economy economy;

    public PayCommand(Economy economy) {
        this.economy = economy;
    }

    @Execute
        // DODANO @Context
    void pay(@Context Player player, @Arg("gracz") Player target, @Arg("kwota") double amount) {
        // Sprawdzenie czy gracz nie przylewa samemu sobie
        if (player.getUniqueId().equals(target.getUniqueId())) {
            PlayerMessage.message(player, MessageType.CHAT, "<red>Nie możesz przelać pieniędzy samemu sobie!");
            SoundsUtil.error(player);
            return;
        }

        // Sprawdzenie kwoty
        if (amount <= 0) {
            PlayerMessage.message(player, MessageType.CHAT, "<red>Podaj prawidłową kwotę większą od 0!");
            SoundsUtil.error(player);
            return;
        }

        // Sprawdzenie stanu konta
        if (!economy.has(player, amount)) {
            PlayerMessage.message(player, MessageType.CHAT, "<red>Nie masz wystarczająco środków na koncie!");
            SoundsUtil.error(player);
            return;
        }

        // Proces przelewu
        economy.withdrawPlayer(player, amount);
        economy.depositPlayer(target, amount);

        // Wiadomość dla nadawcy
        PlayerMessage.message(player, MessageType.CHAT, "<green>Przelałeś <yellow>{amount} <green>do <white>{player}", Map.of(
                "amount", economy.format(amount),
                "player", target.getName()
        ));
        SoundsUtil.accept(player);

        // Wiadomość dla odbiorcy
        PlayerMessage.message(target, MessageType.CHAT, "<green>Otrzymałeś <yellow>{amount} <green>od <white>{player}", Map.of(
                "amount", economy.format(amount),
                "player", player.getName()
        ));
        SoundsUtil.orb(target);
    }
}
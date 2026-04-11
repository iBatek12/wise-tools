package pl.batek.command.commands.tools;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.entity.Player;
import pl.batek.message.MessageType;
import pl.batek.message.PlayerMessage;
import pl.batek.util.SoundsUtil;

import java.util.Map;

@Command(name = "invsee", aliases = {"eq", "inventory"})
@Permission("core.invsee")
public class InvseeCommand {

    @Execute
    void execute(@Context Player player, @Arg("gracz") Player target) {
        // Zabezpieczenie przed otwieraniem własnego eq (co mogłoby zbugować grę)
        if (player.getUniqueId().equals(target.getUniqueId())) {
            PlayerMessage.message(player, MessageType.CHAT, "<red>Nie możesz otworzyć własnego ekwipunku w ten sposób!");
            SoundsUtil.error(player);
            return;
        }

        // Otwieramy ekwipunek celu
        player.openInventory(target.getInventory());

        PlayerMessage.message(player, MessageType.CHAT, "<green>Otwarto ekwipunek gracza <yellow>{player}<green>.",
                Map.of("player", target.getName()));
        SoundsUtil.gui(player);
    }
}
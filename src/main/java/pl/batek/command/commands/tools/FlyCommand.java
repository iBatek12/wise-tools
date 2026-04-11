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

@Command(name = "fly")
@Permission("wise.fly")
public class FlyCommand {

    @Execute
    void toggleSelf(@Context Player player) {
        boolean currentState = player.getAllowFlight();
        player.setAllowFlight(!currentState);

        if (!currentState) {
            PlayerMessage.message(player, MessageType.CHAT, "<green>Latanie zostało <bold>WŁĄCZONE</bold>.");
            SoundsUtil.accept(player);
        } else {
            PlayerMessage.message(player, MessageType.CHAT, "<red>Latanie zostało <bold>WYŁĄCZONE</bold>.");
            SoundsUtil.glass(player);
        }
    }

    @Execute
    void toggleOther(@Context Player sender, @Arg("gracz") Player target) {
        boolean currentState = target.getAllowFlight();
        target.setAllowFlight(!currentState);

        String state = !currentState ? "<green>WŁĄCZONE" : "<red>WYŁĄCZONE";

        PlayerMessage.message(sender, MessageType.CHAT, "<gray>Latanie gracza <yellow>{player} <gray>zostało " + state, Map.of("player", target.getName()));
        PlayerMessage.message(target, MessageType.CHAT, "<gray>Twoje latanie zostało " + state + " <gray>przez <yellow>{admin}", Map.of("admin", sender.getName()));
    }
}
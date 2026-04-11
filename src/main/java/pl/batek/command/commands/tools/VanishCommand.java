package pl.batek.command.commands.tools;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.entity.Player;
import pl.batek.manager.VanishManager;
import pl.batek.message.MessageType;
import pl.batek.message.PlayerMessage;
import pl.batek.util.SoundsUtil;

@Command(name = "vanish", aliases = {"v"})
@Permission("wise.vanish")
public class VanishCommand {

    private final VanishManager vanishManager;

    public VanishCommand(VanishManager vanishManager) {
        this.vanishManager = vanishManager;
    }

    @Execute
    void execute(@Context Player player) {
        boolean isVanished = vanishManager.toggleVanish(player);

        if (isVanished) {
            PlayerMessage.message(player, MessageType.CHAT, "<green>Jesteś teraz niewidoczny dla innych graczy.");
            SoundsUtil.accept(player);
        } else {
            PlayerMessage.message(player, MessageType.CHAT, "<red>Jesteś teraz widoczny dla innych graczy.");
            SoundsUtil.glass(player);
        }
    }
}
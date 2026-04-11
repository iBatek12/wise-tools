package pl.batek.command.commands;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.entity.Player;

@Command(name = "anvil")
public class AnvilCommand {

    @Execute
    void executeAnvil(@Context Player player) {
        player.openAnvil(null, true);
    }

}

package pl.batek.command.commands;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.entity.Player;
import pl.batek.util.AdventureUtil;

@Command(name = "pomoc")
public class PomocCommand {

    @Execute
    void executePomoc(@Context Player player) {
        player.sendMessage(AdventureUtil.translate("&8>> &7Lista komend: &fpomoc&7, &fhome&7, &f/tpa&7, &f/msg&7, &f/ec&7, &f/darmowerzeczy"));
    }

}

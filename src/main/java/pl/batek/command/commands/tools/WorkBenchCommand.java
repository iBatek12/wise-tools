package pl.batek.command.commands.tools;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.entity.Player;

@Command(name = "workbench", aliases = {"wb"})
public class WorkBenchCommand {

    @Execute
    void executeWorkBench(@Context Player player) {
        player.openWorkbench(null, true);
    }

}
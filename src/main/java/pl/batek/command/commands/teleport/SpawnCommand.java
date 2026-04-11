package pl.batek.command.commands.teleport;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.entity.Player;
import pl.batek.manager.SpawnManager;

@Command(name = "spawn")
public class SpawnCommand {

    private final SpawnManager spawnManager;

    public SpawnCommand(SpawnManager spawnManager) {
        this.spawnManager = spawnManager;
    }

    @Execute
    void execute(@Context Player player) {
        spawnManager.startTeleport(player);
    }
}
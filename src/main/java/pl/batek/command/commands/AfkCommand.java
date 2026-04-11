package pl.batek.command.commands;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.entity.Player;
import pl.batek.config.Configuration;
import pl.batek.menu.menus.AfkGui;

@Command(name = "afk")
public class AfkCommand {

    private final Configuration config;

    public AfkCommand(Configuration config) {
        this.config = config;
    }

    @Execute
    void execute(@Context Player player) {
        new AfkGui(config).show(player);
    }
}
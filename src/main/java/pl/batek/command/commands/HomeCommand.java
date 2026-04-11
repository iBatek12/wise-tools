package pl.batek.command.commands;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.entity.Player;
import pl.batek.manager.HomeManager;
import pl.batek.menu.menus.HomeGui;

@Command(name = "home", aliases = {"dom", "domy", "sethome", "ustawdom"})
public class HomeCommand {

    private final HomeManager homeManager;

    public HomeCommand(HomeManager homeManager) {
        this.homeManager = homeManager;
    }

    @Execute
    void execute(@Context Player player) {
        new HomeGui(homeManager).show(player);
    }
}
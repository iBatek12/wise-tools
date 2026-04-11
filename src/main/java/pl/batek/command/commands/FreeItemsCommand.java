package pl.batek.command.commands;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.entity.Player;
import pl.batek.manager.FreeItemsManager;
import pl.batek.menu.menus.FreeItemsGui;

@Command(name = "freeitems", aliases = "darmowerzeczy")
public class FreeItemsCommand {

    private final FreeItemsManager freeItemsManager;

    public FreeItemsCommand(FreeItemsManager freeItemsManager) {
        this.freeItemsManager = freeItemsManager;
    }

    @Execute
    void execute(@Context Player player) {
        new FreeItemsGui(freeItemsManager).show(player);
    }
}
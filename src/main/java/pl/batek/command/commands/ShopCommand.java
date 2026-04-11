package pl.batek.command.commands;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.entity.Player;
import pl.batek.economy.EconomyManager;
import pl.batek.manager.PlaytimeManager;
import pl.batek.menu.menus.shop.ShopGui;

@Command(name = "sklep", aliases = {"shop"})
public class ShopCommand {

    private final EconomyManager economyManager;
    private final PlaytimeManager playtimeManager;

    public ShopCommand(EconomyManager economyManager, PlaytimeManager playtimeManager) {
        this.economyManager = economyManager;
        this.playtimeManager = playtimeManager;
    }

    @Execute
    void execute(@Context Player player) {
        new ShopGui(economyManager, playtimeManager).show(player);
    }
}
package pl.batek.command.commands.tools;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import pl.batek.util.AdventureUtil;
import pl.batek.util.SoundsUtil;

@Command(name = "kosz", aliases = {"trash", "smietnik"})
public class TrashCommand {

    @Execute
    void execute(@Context Player player) {
        Component title = AdventureUtil.miniMessage("<dark_gray><b>Kosz na śmieci</b>", null);
        Inventory trash = Bukkit.createInventory(null, 54, title);

        player.openInventory(trash);
        SoundsUtil.shulkerOpen(player);
    }
}
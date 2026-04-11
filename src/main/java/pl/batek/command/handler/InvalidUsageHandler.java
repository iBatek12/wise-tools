package pl.batek.command.handler;

import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invalidusage.InvalidUsage;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.schematic.Schematic;
import java.util.Iterator;
import org.bukkit.command.CommandSender;
import pl.batek.util.AdventureUtil;

public class InvalidUsageHandler implements dev.rollczi.litecommands.invalidusage.InvalidUsageHandler<CommandSender> {
    public void handle(Invocation<CommandSender> invocation, InvalidUsage<CommandSender> result, ResultHandlerChain<CommandSender> chain) {
        CommandSender sender = (CommandSender)invocation.sender();
        Schematic schematic = result.getSchematic();
        if (schematic.isOnlyFirst()) {
            sender.sendMessage(AdventureUtil.translate("&8[&#FF0000❌&8] &7Poprawne użycie: &f" + schematic.first()));
        } else {
            sender.sendMessage(AdventureUtil.translate("&8[&#FF0000❌&8] &7Poprawne użycie: &f"));
            Iterator var6 = schematic.all().iterator();

            while(var6.hasNext()) {
                String scheme = (String)var6.next();
                sender.sendMessage(AdventureUtil.translate("&8→ &f" + scheme));
            }

        }
    }
}

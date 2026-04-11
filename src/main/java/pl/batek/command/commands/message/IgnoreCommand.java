package pl.batek.command.commands.message;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.entity.Player;
import pl.batek.manager.MessageManager;
import pl.batek.util.AdventureUtil;

@Command(name = "ignore", aliases = {"msgtoggle"})
public class IgnoreCommand {

    private final MessageManager messageManager;

    public IgnoreCommand(MessageManager messageManager) {
        this.messageManager = messageManager;
    }

    @Execute
    void executeIgnoreAll(@Context Player player) {
        toggleGlobal(player);
    }

    @Execute(name = "*")
    void executeIgnoreStar(@Context Player player) {
        toggleGlobal(player);
    }

    @Execute
    void executeIgnorePlayer(@Context Player player, @Arg("nick") Player target) {
        if (player.equals(target)) {
            player.sendMessage(AdventureUtil.translate("&cNie możesz zignorować samego siebie."));
            return;
        }

        boolean isIgnored = messageManager.toggleIgnore(player.getUniqueId(), target.getUniqueId());
        if (isIgnored) {
            player.sendMessage(AdventureUtil.translate("&cZignorowałeś &7gracza &c" + target.getName() + "&7."));
        } else {
            player.sendMessage(AdventureUtil.translate("&aOdblokowałeś &7gracza &a" + target.getName() + "&7."));
        }
    }

    private void toggleGlobal(Player player) {
        boolean isIgnoringAll = messageManager.toggleGlobalIgnore(player.getUniqueId());
        if (isIgnoringAll) {
            player.sendMessage(AdventureUtil.translate("&cWyłączyłeś &7otrzymywanie wiadomości prywatnych."));
        } else {
            player.sendMessage(AdventureUtil.translate("&aWłączyłeś &7otrzymywanie wiadomości prywatnych."));
        }
    }
}
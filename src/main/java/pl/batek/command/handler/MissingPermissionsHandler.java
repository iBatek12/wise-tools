package pl.batek.command.handler;

import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.permission.MissingPermissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.batek.util.AdventureUtil;
import pl.batek.util.SoundsUtil;

public class MissingPermissionsHandler implements dev.rollczi.litecommands.permission.MissingPermissionsHandler<CommandSender> {
    public void handle(Invocation<CommandSender> invocation, MissingPermissions missingPermissions, ResultHandlerChain<CommandSender> chain) {
        String permissions = missingPermissions.asJoinedText();
        CommandSender sender = (CommandSender)invocation.sender();
        SoundsUtil.error((Player)sender);
        sender.sendMessage(AdventureUtil.translate("&8[&#FF0000❌&8]  &cNie posiadasz uprawnienia &#FF0000(" + permissions + ")"));
    }
}

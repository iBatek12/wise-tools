package pl.batek.command.commands.message;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.join.Join;
import org.bukkit.entity.Player;
import pl.batek.manager.MessageManager;
import pl.batek.util.AdventureUtil;

@Command(name = "message", aliases = {"msg", "tell", "w", "whisper"})
public class MessageCommand {

    private final MessageManager messageManager;

    public MessageCommand(MessageManager messageManager) {
        this.messageManager = messageManager;
    }

    @Execute
    void executeMessage(@Context Player sender, @Arg("nick") Player target, @Join("treść") String message) {
        if (sender.equals(target)) {
            sender.sendMessage(AdventureUtil.translate("&cNie możesz wysłać wiadomości do samego siebie!"));
            return;
        }

        if (!messageManager.canReceiveMessage(sender.getUniqueId(), target.getUniqueId())) {
            sender.sendMessage(AdventureUtil.translate("&cTen gracz ignoruje wiadomości prywatne."));
            return;
        }


        sender.sendMessage(AdventureUtil.translate("&8[&eTy &8-> &c" + target.getName() + "&8] &f" + message));
        target.sendMessage(AdventureUtil.translate("&8[&c" + sender.getName() + " &8-> &eTy&8] &f" + message));
        messageManager.setLastConversation(sender.getUniqueId(), target.getUniqueId());
    }
}
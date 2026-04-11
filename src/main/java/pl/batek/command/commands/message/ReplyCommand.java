package pl.batek.command.commands.message;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.join.Join;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.batek.manager.MessageManager;
import pl.batek.util.AdventureUtil;

import java.util.UUID;

@Command(name = "reply", aliases = {"r", "odpowiedz"})
public class ReplyCommand {
    private final MessageManager messageManager;

    public ReplyCommand(MessageManager messageManager) {
        this.messageManager = messageManager;
    }

    @Execute
    void executeReply(@Context Player sender, @Join("treść") String message) {
        UUID targetId = messageManager.getLastMessaged(sender.getUniqueId());

        if (targetId == null) {
            sender.sendMessage(AdventureUtil.translate("&cNie masz komu odpowiedzieć."));
            return;
        }

        Player target = Bukkit.getPlayer(targetId);
        if (target == null || !target.isOnline()) {
            sender.sendMessage(AdventureUtil.translate("&cGracz z którym ostatnio pisałeś jest teraz offline."));
            return;
        }

        if (!messageManager.canReceiveMessage(sender.getUniqueId(), target.getUniqueId())) {
            sender.sendMessage(AdventureUtil.translate("&cTen gracz ignoruje wiadomości prywatne."));
            return;
        }

        // Wysłanie wiadomości
        sender.sendMessage(AdventureUtil.translate("&8[&eTy &8-> &c" + target.getName() + "&8] &f" + message));
        target.sendMessage(AdventureUtil.translate("&8[&c" + sender.getName() + " &8-> &eTy&8] &f" + message));

        // Odświeżenie konwersacji w pamięci
        messageManager.setLastConversation(sender.getUniqueId(), target.getUniqueId());
    }
}
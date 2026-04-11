package pl.batek.command.commands.tools;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.batek.manager.ChatManager;
import pl.batek.util.AdventureUtil;

@Command(name = "chat", aliases = {"c"})
@Permission("wisemc.command.chat")
public class ChatCommand {

    private final ChatManager chatManager;

    public ChatCommand(ChatManager chatManager) {
        this.chatManager = chatManager;
    }

    @Execute(name = "on")
    void executeOn(@Context CommandSender sender) {
        if (chatManager.isChatEnabled()) {
            sender.sendMessage(AdventureUtil.translate("&cChat jest już włączony!"));
            return;
        }

        chatManager.setChatEnabled(true);
        broadcast(AdventureUtil.translate("&aChat został włączony przez &7" + sender.getName() + "&a!"));
    }

    @Execute(name = "off")
    void executeOff(@Context CommandSender sender) {
        if (!chatManager.isChatEnabled()) {
            sender.sendMessage(AdventureUtil.translate("&cChat jest już wyłączony!"));
            return;
        }

        chatManager.setChatEnabled(false);
        broadcast(AdventureUtil.translate("&cChat został wyłączony przez &7" + sender.getName() + "&c!"));
    }

    @Execute(name = "clear")
    void executeClear(@Context CommandSender sender) {
        Component emptyLine = Component.text(" ");
        for (int i = 0; i < 100; i++) {
            broadcast(emptyLine);
        }

        broadcast(AdventureUtil.translate("&bChat został wyczyszczony przez &7" + sender.getName() + "&b!"));
    }

    private void broadcast(Component component) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(component);
        }
        Bukkit.getConsoleSender().sendMessage(component);
    }
}
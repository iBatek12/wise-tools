package pl.batek.command.commands.tools;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.join.Join;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.batek.message.MessageType;
import pl.batek.message.PlayerMessage;
import pl.batek.util.SoundsUtil;

@Command(name = "broadcast", aliases = {"bc", "ogloszenie"})
@Permission("wisemc.command.broadcast")
public class BroadcastCommand {

    @Execute
    void execute(@Context CommandSender sender, @Join("wiadomość") String message) {
        String formattedMessage = message.replace("\\n", "\n");
        if (!formattedMessage.contains("\n")) {
            formattedMessage = "<dark_red><bold>OGŁOSZENIE</bold></dark_red>\n" + formattedMessage;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerMessage.message(player, MessageType.TITLE_SUBTITLE, formattedMessage);
            SoundsUtil.dragon(player);
        }}
}
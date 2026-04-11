package pl.batek.command.commands.tools;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.join.Join;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.batek.message.MessageType;
import pl.batek.message.PlayerMessage;
import pl.batek.util.AdventureUtil;
import pl.batek.util.SoundsUtil;

@Command(name = "helpop")
public class HelpopCommand {

    @Execute
    void execute(@Context Player player, @Join("treść") String message) {
        // 1. Wiadomość dla gracza wysyłającego
        player.sendMessage(AdventureUtil.translate("&aTwoja wiadomość została wysłana do administracji!"));
        SoundsUtil.accept(player);

        // 2. Formatowanie wiadomości dla administracji
        // Używamy \n aby PlayerMessage rozpoznał podział na Title i Subtitle
        String titleContent = "&d&lHELPOP\n&c" + message;
        String chatContent = "&8[&d&lHELPOP&8] &7" + player.getName() + " &8» &f" + message;

        // 3. Wysyłanie do wszystkich administratorów (osób z odpowiednią permisją)
        for (Player admin : Bukkit.getOnlinePlayers()) {
            if (admin.hasPermission("wisemc.helpop.receive")) {

                // Wysyłamy Title i Subtitle (korzystając z Twojej klasy)
                PlayerMessage.message(admin, MessageType.TITLE_SUBTITLE, titleContent);

                // Wysyłamy wiadomość na Chat
                admin.sendMessage(AdventureUtil.translate(chatContent));

                // Opcjonalnie dźwięk dla admina, żeby nie przegapił
                SoundsUtil.dragon(admin);
            }
        }

        // Log do konsoli
        Bukkit.getConsoleSender().sendMessage(AdventureUtil.translate(chatContent));
    }
}
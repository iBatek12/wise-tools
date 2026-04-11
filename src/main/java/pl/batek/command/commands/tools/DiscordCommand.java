package pl.batek.command.commands.tools;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.entity.Player;
import pl.batek.util.AdventureUtil;

@Command(name = "discord", aliases = {"dc"})
public class DiscordCommand {

    @Execute
    void executeDiscord(@Context Player player) {
        player.sendMessage(AdventureUtil.translate(""));
        player.sendMessage(AdventureUtil.miniMessage("<dark_gray>→ <white>discord: <light_purple><click:open_url:'https://dc.wisemc.pl'><hover:show_text:'<white>Kliknij, aby dołączyć!'>dc.wisemc.pl</hover></click>", null));
        player.sendMessage(AdventureUtil.translate(""));
    }

}
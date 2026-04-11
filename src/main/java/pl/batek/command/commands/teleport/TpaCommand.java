package pl.batek.command.commands.teleport;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.entity.Player;
import pl.batek.manager.TpaManager;
import pl.batek.util.AdventureUtil;

@Command(name = "tpa")
public class TpaCommand {

    private final TpaManager tpaManager;

    public TpaCommand(TpaManager tpaManager) {
        this.tpaManager = tpaManager;
    }

    @Execute
    void execute(@Context Player player, @Arg("gracz") Player target) {
        if (player.equals(target)) {
            player.sendMessage(AdventureUtil.translate("&cNie możesz wysłać prośby do samego siebie!"));
            return;
        }

        if (!tpaManager.sendRequest(player, target)) {
            player.sendMessage(AdventureUtil.translate("&cWysłałeś już prośbę o teleportację do tego gracza. Poczekaj aż wygaśnie!"));
            return;
        }

        // Informacja dla nadawcy
        player.sendMessage(AdventureUtil.translate("&aWysłano prośbę o teleportację do gracza &2" + target.getName()));

        // Interaktywna informacja dla odbiorcy
        target.sendMessage(AdventureUtil.translate(""));
        target.sendMessage(AdventureUtil.translate(" &8× &7Gracz &a" + player.getName() + " &7wysłał ci prośbę o &fteleportację!"));

        // Tutaj używamy potęgi MiniMessage z Twojego AdventureUtil – [ZAAKCEPTUJ] będzie klikalne!
        target.sendMessage(AdventureUtil.miniMessage(" <dark_gray>× <gray>Aby <green>zaakceptować <gray>kliknij: <white><click:run_command:'/tpaccept " + player.getName() + "'><hover:show_text:'<green>Kliknij, aby przeteleportować gracza!'><b>[ZAAKCEPTUJ]</b></hover></click>", null));

        target.sendMessage(AdventureUtil.translate(""));
    }
}
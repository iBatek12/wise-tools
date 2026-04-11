package pl.batek.command.commands.teleport;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.entity.Player;
import pl.batek.util.AdventureUtil;

@Command(name = "stp", aliases = {"s", "tphere"})
@Permission("wisemc.command.stp") // Tylko dla administracji
public class StpCommand {

    @Execute
    void execute(@Context Player admin, @Arg("gracz") Player target) {
        // Zabezpieczenie przed teleportacją samego siebie
        if (admin.equals(target)) {
            admin.sendMessage(AdventureUtil.translate("&cNie możesz przeteleportować samego siebie!"));
            return;
        }

        // Teleportacja gracza do lokalizacji administratora
        target.teleport(admin.getLocation());

        // Wiadomość dla administratora
        admin.sendMessage(AdventureUtil.translate("&aPrzeteleportowano gracza &7" + target.getName() + " &ado Ciebie."));

        // Wiadomość dla przeteleportowanego gracza (opcjonalna, możesz ją usunąć, jeśli ma to być ciche)
        target.sendMessage(AdventureUtil.translate("&8>> &7Zostałeś przeteleportowany do administratora &c" + admin.getName() + "&7."));
    }
}
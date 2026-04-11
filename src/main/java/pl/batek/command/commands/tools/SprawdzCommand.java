package pl.batek.command.commands.tools;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.batek.config.Configuration;
import pl.batek.manager.SprawdzManager;
import pl.batek.message.MessageType;
import pl.batek.message.PlayerMessage;
import pl.batek.util.AdventureUtil;
import pl.batek.util.SoundsUtil;

@Command(name = "sprawdz")
@Permission("wisemc.command.sprawdz")
public class SprawdzCommand {

    public enum Akcja {
        WEZWIJ, CHEATY, BRAKWSPOLPRACY, PRZYZNANIESIE, CZYSTY
    }

    private final Configuration config;
    private final JavaPlugin plugin;
    private final SprawdzManager sprawdzManager;

    public SprawdzCommand(Configuration config, JavaPlugin plugin, SprawdzManager sprawdzManager) {
        this.config = config;
        this.plugin = plugin;
        this.sprawdzManager = sprawdzManager;
    }

    @Execute
    void execute(@Context Player admin, @Arg("gracz") Player target, @Arg("akcja") Akcja akcja) {

        boolean isSprawdzany = sprawdzManager.isSprawdzany(target.getUniqueId());

        // Blokady (Guard clauses)
        if (akcja == Akcja.WEZWIJ) {
            if (isSprawdzany) {
                admin.sendMessage(AdventureUtil.translate("&cTen gracz jest już sprawdzany!"));
                SoundsUtil.error(admin);
                return;
            }
        } else {
            // Dla każdej innej opcji (CZYSTY, CHEATY, itp.) gracz MUSI być najpierw sprawdzany
            if (!isSprawdzany) {
                admin.sendMessage(AdventureUtil.translate("&cTen gracz nie jest aktualnie sprawdzany! Najpierw użyj: &7/sprawdz " + target.getName() + " wezwij"));
                SoundsUtil.error(admin);
                return;
            }
        }

        Location spawnLoc = getSpawnLocation();
        Location sprawdzarkaLoc = getSprawdzarkaLocation();

        switch (akcja) {
            case WEZWIJ:
                sprawdzManager.setSprawdzany(target.getUniqueId(), true);
                admin.teleport(sprawdzarkaLoc);
                target.teleport(sprawdzarkaLoc);

                PlayerMessage.message(target, MessageType.TITLE_SUBTITLE, "<red><bold>JESTEŚ SPRAWDZANY!</bold>\n<gray>Zastosuj się do poleceń administracji na czacie.");
                admin.sendMessage(AdventureUtil.translate("&aPomyślnie wezwano gracza &7" + target.getName() + " &ado sprawdzarki."));
                SoundsUtil.dragon(target);
                break;

            case CZYSTY:
                sprawdzManager.setSprawdzany(target.getUniqueId(), false);
                target.teleport(spawnLoc);
                PlayerMessage.message(target, MessageType.TITLE_SUBTITLE, "<green><bold>JESTEŚ CZYSTY!</bold>\n<gray>Dziękujemy za współpracę.");
                admin.sendMessage(AdventureUtil.translate("&aZwolniono gracza &7" + target.getName() + " &az podejrzenia o cheaty."));
                SoundsUtil.celebrate(target);
                break;

            case CHEATY:
                sprawdzManager.setSprawdzany(target.getUniqueId(), false);
                banPlayer(target, spawnLoc, "Cheaty / Niedozwolone modyfikacje");
                admin.sendMessage(AdventureUtil.translate("&cZbanowano gracza &7" + target.getName() + " &cza cheaty."));
                break;

            case BRAKWSPOLPRACY:
                sprawdzManager.setSprawdzany(target.getUniqueId(), false);
                banPlayer(target, spawnLoc, "Brak współpracy podczas sprawdzania");
                admin.sendMessage(AdventureUtil.translate("&cZbanowano gracza &7" + target.getName() + " &cza brak współpracy."));
                break;

            case PRZYZNANIESIE:
                sprawdzManager.setSprawdzany(target.getUniqueId(), false);
                banPlayer(target, spawnLoc, "Przyznanie się do używania cheatów");
                admin.sendMessage(AdventureUtil.translate("&cZbanowano gracza &7" + target.getName() + " &cza przyznanie się."));
                break;
        }
    }

    private void banPlayer(Player target, Location spawnLoc, String reason) {
        target.teleport(spawnLoc);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban " + target.getName() + " " + reason);
        }, 5L);
    }

    private Location getSpawnLocation() {
        World world = Bukkit.getWorld(config.spawnWorld);
        if (world == null) world = Bukkit.getWorlds().get(0);
        return new Location(world, config.spawnX, config.spawnY, config.spawnZ, config.spawnYaw, config.spawnPitch);
    }

    private Location getSprawdzarkaLocation() {
        World world = Bukkit.getWorld(config.sprawdzarkaWorld);
        if (world == null) world = Bukkit.getWorlds().get(0);
        return new Location(world, config.sprawdzarkaX, config.sprawdzarkaY, config.sprawdzarkaZ, config.sprawdzarkaYaw, config.sprawdzarkaPitch);
    }
}
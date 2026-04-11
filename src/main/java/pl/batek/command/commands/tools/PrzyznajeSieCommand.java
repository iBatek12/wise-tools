package pl.batek.command.commands.tools;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.batek.config.Configuration;
import pl.batek.manager.SprawdzManager;
import pl.batek.util.AdventureUtil;
import pl.batek.util.SoundsUtil;

@Command(name = "przyznajesie", aliases = {"przyznajsie"})
public class PrzyznajeSieCommand {

    private final Configuration config;
    private final JavaPlugin plugin;
    private final SprawdzManager sprawdzManager;

    public PrzyznajeSieCommand(Configuration config, JavaPlugin plugin, SprawdzManager sprawdzManager) {
        this.config = config;
        this.plugin = plugin;
        this.sprawdzManager = sprawdzManager;
    }

    @Execute
    void execute(@Context Player player) {

        // Zabezpieczenie: Czy gracz na pewno jest sprawdzany?
        if (!sprawdzManager.isSprawdzany(player.getUniqueId())) {
            player.sendMessage(AdventureUtil.translate("&cNie jesteś aktualnie sprawdzany!"));
            SoundsUtil.error(player);
            return;
        }

        sprawdzManager.setSprawdzany(player.getUniqueId(), false);

        // 1. Pobieramy lokację spawna
        World world = Bukkit.getWorld(config.spawnWorld);
        if (world == null) world = Bukkit.getWorlds().get(0);
        Location spawnLoc = new Location(world, config.spawnX, config.spawnY, config.spawnZ, config.spawnYaw, config.spawnPitch);

        // 2. Teleportacja na spawn
        player.teleport(spawnLoc);

        // 3. Informacja dla administracji
        for (Player admin : Bukkit.getOnlinePlayers()) {
            if (admin.hasPermission("wisemc.command.sprawdz")) {
                admin.sendMessage(AdventureUtil.translate("&8[&c&l!&8] &cGracz &7" + player.getName() + " &cprzyznał się do cheatów używając &7/przyznajesie&c!"));
            }
        }

        // 4. Ban z opóźnieniem
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban " + player.getName() + " Przyznanie się do używania cheatów");
        }, 5L);
    }
}
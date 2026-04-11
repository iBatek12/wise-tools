package pl.batek.controller;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.batek.manager.SprawdzManager;
import pl.batek.util.AdventureUtil;
import pl.batek.util.SoundsUtil;

public class SprawdzController implements Listener {

    private final SprawdzManager sprawdzManager;

    public SprawdzController(SprawdzManager sprawdzManager) {
        this.sprawdzManager = sprawdzManager;
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        // Jeśli gracz nie jest sprawdzany, ignorujemy (może używać komend)
        if (!sprawdzManager.isSprawdzany(player.getUniqueId())) return;

        // Pobieramy komendę bazową (np. /msg z "/msg siema")
        String[] args = event.getMessage().toLowerCase().split(" ");
        String cmd = args[0];

        // Dozwolone komendy
        if (cmd.equals("/msg") || cmd.equals("/w") || cmd.equals("/tell") || cmd.equals("/r") ||
                cmd.equals("/helpop") || cmd.equals("/przyznajesie") || cmd.equals("/przyznajsie")) {
            return;
        }

        // Zablokowanie każdej innej komendy
        event.setCancelled(true);
        player.sendMessage(AdventureUtil.translate("&cNie możesz używać tej komendy podczas sprawdzania!"));
        player.sendMessage(AdventureUtil.translate("&cDozwolone: &7/msg, /r, /helpop, /przyznajesie"));
        SoundsUtil.error(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Jeśli gracz wyjdzie z serwera podczas sprawdzania -> AUTO-BAN
        if (sprawdzManager.isSprawdzany(player.getUniqueId())) {
            sprawdzManager.setSprawdzany(player.getUniqueId(), false);

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban " + player.getName() + " Wylogowanie się podczas sprawdzania");

            for (Player admin : Bukkit.getOnlinePlayers()) {
                if (admin.hasPermission("wisemc.command.sprawdz")) {
                    admin.sendMessage(AdventureUtil.translate("&8[&c&l!&8] &cGracz &7" + player.getName() + " &cwylogował się podczas sprawdzania i został zbanowany!"));
                }
            }
        }
    }
}
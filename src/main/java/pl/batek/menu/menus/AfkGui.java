package pl.batek.menu.menus;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.batek.config.Configuration;
import pl.batek.menu.GuiCreator;
import pl.batek.util.AdventureUtil;
import pl.batek.util.SoundsUtil;

import java.util.Arrays;

public class AfkGui extends GuiCreator {

    private final Configuration config;

    public AfkGui(Configuration config) {
        super("&8Strefa AFK", 5);
        this.config = config;
    }

    @Override
    public void setContents(Player p) {
        setItem(23, createGuiItem(Material.CAMPFIRE, "&cTeleport na strefę AFK", "&7Kliknij, aby przenieść się", "&7na bezpieczną strefę."));
        setItem(1, createGuiItem(Material.PINK_STAINED_GLASS_PANE, ""));
    }

    @Override
    public void handleClickAction(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getSlot() == 22) {
            p.closeInventory();

            World world = Bukkit.getWorld(config.afkWorld);
            if (world == null) {
                world = p.getWorld();
            }

            Location afkLoc = new Location(world, config.afkX, config.afkY, config.afkZ, config.afkYaw, config.afkPitch);
            p.teleport(afkLoc);

            p.sendMessage(AdventureUtil.translate("&8>> &7Zostałeś przeniesiony na strefę &cAFK&7."));
            SoundsUtil.accept(p);
        }
    }
}
package pl.batek.menu.menus;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import pl.batek.manager.HomeManager;
import pl.batek.menu.GuiCreator;
import pl.batek.message.MessageType;
import pl.batek.message.PlayerMessage;

import java.util.Map;

public class HomeGui extends GuiCreator {
    private final HomeManager homeManager;
    private final int[] homeSlots = {21, 22, 23, 24, 25};
    private final Material[] bedColors = {
            Material.WHITE_BED, Material.MAGENTA_BED, Material.LIGHT_BLUE_BED, Material.YELLOW_BED, Material.LIME_BED
    };

    public HomeGui(HomeManager homeManager) {
        super("&8Twoje domki", 5);
        this.homeManager = homeManager;
    }

    @Override
    public void setContents(Player p) {
        Map<Integer, Location> homes = homeManager.getHomes(p.getUniqueId());

        for (int i = 0; i < 5; i++) {
            int homeId = i + 1;
            int slot = homeSlots[i];

            if (homes.containsKey(homeId)) {
                setItem(slot, createGuiItem(
                        bedColors[i],
                        "&fDomek: &d#" + homeId,
                        "",
                        "&8» &7Kliknij &dLPM&7, aby się przeteleportować",
                        "&8» &7Kliknij &dPPM&7, aby zarządzać domkiem (usuń)"
                ));
            } else {
                setItem(slot, createGuiItem(
                        Material.RED_BED,
                        "&fDomek: &d#" + homeId + " &7(Pusty)",
                        "",
                        "&8» &7Kliknij &dLPM&7, aby &austawić &7domek w tym miejscu"
                ));
            }
        }

        setItem(41, createGuiItem(Material.BARRIER, "&cAnuluj"));
    }

    @Override
    public void handleClickAction(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        int slot = e.getSlot() + 1;
        if (slot == 41) {
            p.closeInventory();
            return;
        }

        for (int i = 0; i < 5; i++) {
            if (slot == homeSlots[i]) {
                int homeId = i + 1;
                Map<Integer, Location> homes = homeManager.getHomes(p.getUniqueId());

                if (homes.containsKey(homeId)) {
                    if (e.getClick() == ClickType.LEFT) {
                        p.closeInventory();
                        homeManager.teleportToHome(p, homes.get(homeId));
                    } else if (e.getClick() == ClickType.RIGHT) {
                        homeManager.removeHome(p, homeId);
                        setContents(p);
                        PlayerMessage.message(p, MessageType.CHAT, "<green>Pomyślnie usunięto domek!");
                    }
                } else {
                    if (e.getClick() == ClickType.LEFT) {
                        homeManager.setHome(p, homeId);
                        setContents(p);
                        PlayerMessage.message(p, MessageType.CHAT, "<green>Pomyślnie ustawiono domek <yellow>#" + homeId + "<green>!");
                    }
                }
                break;
            }
        }
    }
}
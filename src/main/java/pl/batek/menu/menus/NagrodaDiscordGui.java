package pl.batek.menu.menus;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import pl.batek.manager.DiscordRewardManager;
import pl.batek.menu.GuiCreator;

public class NagrodaDiscordGui extends GuiCreator {
    private final DiscordRewardManager rewardManager;

    public NagrodaDiscordGui(DiscordRewardManager rewardManager) {
        super("&8Nagroda Discord", 3);
        this.rewardManager = rewardManager;
    }

    @Override
    public void setContents(Player p) {
        boolean claimed = rewardManager.hasClaimed(p.getName());

        String status = claimed ? "&aV &7(Odebrano)" : "&cX &7(Nie odebrano)";
        Material icon = claimed ? Material.MINECART : Material.CHEST_MINECART;

        setItem(14, createGuiItem(icon, "&9Nagroda Discord",
                "&7Status: " + status,
                "",
                "&7Aby odebrać nagrodę, wejdź na nasz serwer Discord",
                "&7i kliknij przycisk na kanale nagród!",
                "&7Nagroda: &eVIP na zawsze"
        ));

        int[] glassSlots = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27};
        for (int slot : glassSlots) {
            setItem(slot, createGuiItem(Material.BLUE_STAINED_GLASS_PANE, " "));
        }
    }

    @Override
    public void handleClickAction(InventoryClickEvent e) {
        e.setCancelled(true);
    }
}
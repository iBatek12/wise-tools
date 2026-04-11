package pl.batek.menu.menus.shop;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import pl.batek.economy.EconomyManager;
import pl.batek.manager.PlaytimeManager;
import pl.batek.manager.ShopOffers;
import pl.batek.menu.GuiCreator;
import pl.batek.message.MessageType;
import pl.batek.message.PlayerMessage;
import pl.batek.util.FormatUtil;
import pl.batek.util.SoundsUtil;

import java.util.List;

public class ShopSellGui extends GuiCreator {

    private final EconomyManager economyManager;
    private final PlaytimeManager playtimeManager;
    private final List<ShopOffers.SellOffer> sellOffers;
    private final int[] itemSlots = {22, 23, 24};

    public ShopSellGui(EconomyManager eco, PlaytimeManager pt) {
        super("&8Sprzedaż przedmiotów", 5);
        this.economyManager = eco;
        this.playtimeManager = pt;
        this.sellOffers = ShopOffers.getSellOffers();
    }

    @Override
    public void setContents(Player p) {
        int[] purpleGlass = {1, 9, 37, 45};
        for (int slot : purpleGlass) setItem(slot, createGuiItem(Material.PURPLE_STAINED_GLASS_PANE, " "));

        int[] pinkGlass = {2, 10, 8, 18, 28, 38, 44, 36};
        for (int slot : pinkGlass) setItem(slot, createGuiItem(Material.PINK_STAINED_GLASS_PANE, " "));

        int[] grayGlass = {3, 4, 6, 7,19,27,35, 39, 40, 42, 43};
        for (int slot : grayGlass) setItem(slot, createGuiItem(Material.WHITE_STAINED_GLASS_PANE, " "));

        // --- INFORMACJA O STANIE KONTA ---
        setItem(5, createGuiItem(Material.BOOK, "&d&lINFORMACJE",
                "&7Sprzedaj swoje ciężko zdobyte",
                "&7surowce, aby otrzymać gotówkę.",
                "",
                "&8» &7Twój stan konta: &a" + FormatUtil.formatMoney(economyManager.getBalance(p)) + "$"));

        setItem(41, createGuiItem(Material.BARRIER, "&cPowrót", "&7Kliknij, aby wrócić do głównego menu."));

        for (int i = 0; i < sellOffers.size() && i < itemSlots.length; i++) {
            ShopOffers.SellOffer offer = sellOffers.get(i);
            int slot = itemSlots[i];

            setItem(slot, createGuiItem(offer.getMaterial(), offer.getDisplayName(),
                    "&8» &cInformacje o przedmiocie:",
                    "&8⁑ &7Cena skupu: &c" + FormatUtil.formatMoney(offer.getSellPrice()) + "$",
                    "",
                    "&8» &cKliknij LEWYM, aby sprzedać 1 szt.",
                    "&8» &cKliknij PRAWYM, aby sprzedać cały stack (64)."
            ));
        }
    }

    @Override
    public void handleClickAction(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        int clickedSlot = e.getSlot() + 1;

        if (clickedSlot == 41) {
            new ShopGui(economyManager, playtimeManager).show(p);
            return;
        }

        for (int i = 0; i < sellOffers.size() && i < itemSlots.length; i++) {
            if (clickedSlot == itemSlots[i]) {
                ShopOffers.SellOffer offer = sellOffers.get(i);
                int amount = e.isLeftClick() ? 1 : 64;
                sellItem(p, offer, amount);
                return;
            }
        }
    }

    private void sellItem(Player p, ShopOffers.SellOffer offer, int amountToSell) {
        if (!p.getInventory().containsAtLeast(new ItemStack(offer.getMaterial()), amountToSell)) {
            PlayerMessage.message(p, MessageType.CHAT, "<red>Nie masz wystarczającej ilości tego przedmiotu!");
            SoundsUtil.error(p);
            return;
        }

        p.getInventory().removeItem(new ItemStack(offer.getMaterial(), amountToSell));
        double earnings = Math.round((amountToSell * offer.getSellPrice()) * 100.0) / 100.0;
        economyManager.setBalance(p, economyManager.getBalance(p) + earnings);

        PlayerMessage.message(p, MessageType.CHAT, "<green>Sprzedano <white>x" + amountToSell + " " + offer.getDisplayName() + " <green>za <yellow>" + FormatUtil.formatMoney(earnings) + "$<green>!");
        SoundsUtil.itemshop(p);

        // Odświeżenie GUI po sprzedaży
        setContents(p);
    }
}
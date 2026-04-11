package pl.batek.menu.menus.shop;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import pl.batek.economy.EconomyManager;
import pl.batek.manager.PlaytimeManager;
import pl.batek.manager.ShopOffers;
import pl.batek.menu.GuiCreator;
import pl.batek.message.MessageType;
import pl.batek.message.PlayerMessage;
import pl.batek.util.FormatUtil;
import pl.batek.util.SoundsUtil;

import java.util.List;

public class ShopCategoryGui extends GuiCreator {

    private final EconomyManager economyManager;
    private final PlaytimeManager playtimeManager;
    private final String categoryType;
    private final List<ShopOffers.Offer> offers;

    private final int[] itemSlots = {
            11, 12, 13, 14, 15, 16, 17,
            21, 22, 23, 24, 25
    };

    public ShopCategoryGui(EconomyManager eco, PlaytimeManager pt, String type) {
        super(type.equals("MONEY") ? "&8Sklep za pieniądze" : "&8Sklep za czas", 5);
        this.economyManager = eco;
        this.playtimeManager = pt;
        this.categoryType = type;
        this.offers = ShopOffers.getBuyOffers(type);
    }

    @Override
    public void setContents(Player p) {
        int[] purpleGlass = {1, 9, 37, 45};
        for (int slot : purpleGlass) setItem(slot, createGuiItem(Material.PURPLE_STAINED_GLASS_PANE, " "));

        int[] pinkGlass = {2, 10, 8, 18, 28, 38, 44, 36};
        for (int slot : pinkGlass) setItem(slot, createGuiItem(Material.PINK_STAINED_GLASS_PANE, " "));

        int[] grayGlass = {3, 4, 6, 7, 39, 40, 42, 43};
        for (int slot : grayGlass) setItem(slot, createGuiItem(Material.WHITE_STAINED_GLASS_PANE, " "));

        // --- INFORMACJA O STANIE KONTA ---
        String balanceLine = categoryType.equals("MONEY") ?
                "&8» &7Twój stan konta: &a" + FormatUtil.formatMoney(economyManager.getBalance(p)) + "$" :
                "&8» &7Twoje monety czasu: &e" + playtimeManager.getTimeCoins(p.getUniqueId());

        setItem(5, createGuiItem(Material.BOOK, "&d&lINFORMACJE",
                "&7Wybierz przedmiot z listy,",
                "&7który chcesz zakupić.",
                "",
                balanceLine));

        setItem(41, createGuiItem(Material.BARRIER, "&cPowrót", "&7Kliknij, aby wrócić do głównego menu."));

        String priceSuffix = categoryType.equals("MONEY") ? "$" : " Monet Czasu";
        String priceColor = categoryType.equals("MONEY") ? "&a" : "&e";
        String actionLine = categoryType.equals("MONEY") ? "&8» &aKliknij, aby przejść do zakupu!" : "&8» &aKliknij, aby zakupić!";

        for (int i = 0; i < offers.size() && i < itemSlots.length; i++) {
            ShopOffers.Offer offer = offers.get(i);
            int slot = itemSlots[i];

            String priceStr = categoryType.equals("MONEY") ? FormatUtil.formatMoney(offer.getPrice()) : String.valueOf(offer.getPrice());

            setItem(slot, createGuiItem(
                    offer.getDisplayItem().getType(),
                    offer.getDisplayName(),
                    "&aInformacje o ofercie:",
                    "&8» &7Cena: " + priceColor + priceStr + priceSuffix,
                    "",
                    actionLine
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

        for (int i = 0; i < offers.size() && i < itemSlots.length; i++) {
            if (clickedSlot == itemSlots[i]) {
                ShopOffers.Offer offer = offers.get(i);

                if (categoryType.equals("MONEY")) {
                    new ShopBuyGui(economyManager, playtimeManager, offer, "MONEY").show(p);
                } else {
                    processTimePurchase(p, offer);
                }
                return;
            }
        }
    }

    private void processTimePurchase(Player p, ShopOffers.Offer offer) {
        int price = offer.getPrice();
        if (playtimeManager.getTimeCoins(p.getUniqueId()) < price) {
            PlayerMessage.message(p, MessageType.CHAT, "<red>Nie masz wystarczająco monet czasu!");
            SoundsUtil.error(p);
            return;
        }

        playtimeManager.removeTimeCoins(p.getUniqueId(), price);

        if (offer.isCommand()) {
            String cmd = offer.getCommand().replace("{player}", p.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        } else {
            p.getInventory().addItem(offer.getDisplayItem());
        }

        PlayerMessage.message(p, MessageType.CHAT, "<green>Pomyślnie dokonano zakupu za czas!");
        SoundsUtil.itemshop(p);

        // Odświeżenie GUI (stanu konta)
        setContents(p);
    }
}
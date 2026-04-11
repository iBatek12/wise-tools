package pl.batek.menu.menus.shop;

import org.bukkit.Bukkit;
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

public class ShopBuyGui extends GuiCreator {
    private final EconomyManager economyManager;
    private final PlaytimeManager playtimeManager;
    private final ShopOffers.Offer offer;
    private final String currencyType;
    private int amount = 1;

    public ShopBuyGui(EconomyManager eco, PlaytimeManager pt, ShopOffers.Offer offer, String type) {
        super("&8Zakup przedmiotu", 5);
        this.economyManager = eco;
        this.playtimeManager = pt;
        this.offer = offer;
        this.currencyType = type;
    }

    @Override
    public void setContents(Player p) {
        int totalCost = amount * offer.getPrice();
        String costStr = currencyType.equals("MONEY") ? FormatUtil.formatMoney(totalCost) : String.valueOf(totalCost);
        String costString = currencyType.equals("MONEY") ? "&a" + costStr + "$" : "&e" + totalCost + " Czasu";

        int[] purpleGlass = {1, 9, 37, 45};
        for (int slot : purpleGlass) setItem(slot, createGuiItem(Material.PURPLE_STAINED_GLASS_PANE, " "));

        int[] pinkGlass = {2, 10, 8, 18, 28, 38, 44, 36};
        for (int slot : pinkGlass) setItem(slot, createGuiItem(Material.PINK_STAINED_GLASS_PANE, " "));

        int[] grayGlass = {3, 4, 6, 7, 39, 40, 42, 43};
        for (int slot : grayGlass) setItem(slot, createGuiItem(Material.WHITE_STAINED_GLASS_PANE, " "));

        // --- INFORMACJA O STANIE KONTA ---
        String balanceLine = currencyType.equals("MONEY") ?
                "&8» &7Twój stan konta: &a" + FormatUtil.formatMoney(economyManager.getBalance(p)) + "$" :
                "&8» &7Twoje monety czasu: &e" + playtimeManager.getTimeCoins(p.getUniqueId());

        setItem(5, createGuiItem(Material.IRON_BARS, "&d&lINFORMACJE",
                "&7Wybierz interesującą cię",
                "&7ilość do zakupu.",
                "",
                balanceLine));

        // --- Minusy ---
        ItemStack min64 = createGuiItem(Material.RED_DYE, "&cOdejmij 64"); min64.setAmount(64); setItem(20, min64);
        ItemStack min16 = createGuiItem(Material.RED_DYE, "&cOdejmij 16"); min16.setAmount(16); setItem(21, min16);
        ItemStack min1 = createGuiItem(Material.RED_DYE, "&cOdejmij 1"); min1.setAmount(1); setItem(22, min1);

        // --- Przedmiot ---
        ItemStack displayItem = offer.getDisplayItem();
        displayItem.setAmount(amount);
        setItem(23, displayItem);

        // --- Plusy ---
        ItemStack plus1 = createGuiItem(Material.LIME_DYE, "&aDodaj 1"); plus1.setAmount(1); setItem(24, plus1);
        ItemStack plus16 = createGuiItem(Material.LIME_DYE, "&aDodaj 16"); plus16.setAmount(16); setItem(25, plus16);
        ItemStack plus64 = createGuiItem(Material.LIME_DYE, "&aDodaj 64"); plus64.setAmount(64); setItem(26, plus64);

        // --- Opcje ---
        setItem(31, createGuiItem(Material.CHEST_MINECART, "&aKup więcej", "&8» &aKliknij LPM, aby kupić więcej (na stacki)!"));
        setItem(33, createGuiItem(Material.FIREWORK_STAR, "&aZakup przedmiot",
                "&8» &aInformacje:", "&8⁑ &7Koszt zakupu: " + costString, "&8⁑ &7Wybrana ilość: &ex" + amount, "", "&8» &aKliknij LPM, aby zakupić!"));

        setItem(41, createGuiItem(Material.BARRIER, "&cPowrót", "&7Kliknij, aby wrócić."));
    }

    @Override
    public void handleClickAction(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        int slot = e.getSlot() + 1;

        if (slot == 20) changeAmount(-64, p);
        if (slot == 21) changeAmount(-16, p);
        if (slot == 22) changeAmount(-1, p);
        if (slot == 24) changeAmount(1, p);
        if (slot == 25) changeAmount(16, p);
        if (slot == 26) changeAmount(64, p);

        if (slot == 41) new ShopCategoryGui(economyManager, playtimeManager, currencyType).show(p);

        if (slot == 31) {
            new ShopBuyMoreGui(economyManager, playtimeManager, offer, currencyType).show(p);
        }

        if (slot == 33) {
            buy(p);
        }
    }

    private void changeAmount(int modifier, Player p) {
        amount += modifier;
        if (amount < 1) amount = 1;
        if (amount > 64) amount = 64;
        setContents(p);
        SoundsUtil.gui(p);
    }

    private void buy(Player p) {
        int totalCost = amount * offer.getPrice();

        if (currencyType.equals("MONEY")) {
            if (economyManager.getBalance(p) < totalCost) {
                PlayerMessage.message(p, MessageType.CHAT, "<red>Nie masz wystarczająco pieniędzy!");
                SoundsUtil.error(p);
                return;
            }
            economyManager.setBalance(p, economyManager.getBalance(p) - totalCost);
        } else {
            if (playtimeManager.getTimeCoins(p.getUniqueId()) < totalCost) {
                PlayerMessage.message(p, MessageType.CHAT, "<red>Nie masz wystarczająco monet czasu!");
                SoundsUtil.error(p);
                return;
            }
            playtimeManager.removeTimeCoins(p.getUniqueId(), totalCost);
        }

        if (offer.isCommand()) {
            String cmd = offer.getCommand().replace("{player}", p.getName());
            for (int i = 0; i < amount; i++) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            }
        } else {
            ItemStack itemToGive = offer.getDisplayItem();
            itemToGive.setAmount(amount);
            p.getInventory().addItem(itemToGive);
        }

        PlayerMessage.message(p, MessageType.CHAT, "<green>Pomyślnie dokonano zakupu!");
        SoundsUtil.itemshop(p);
        p.closeInventory();
    }
}
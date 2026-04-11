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

public class ShopBuyMoreGui extends GuiCreator {

    private final EconomyManager economyManager;
    private final PlaytimeManager playtimeManager;
    private final ShopOffers.Offer offer;
    private final String currencyType;

    private final int[] optionSlots = {19, 20, 21, 22, 23, 24, 25, 26, 27};

    public ShopBuyMoreGui(EconomyManager eco, PlaytimeManager pt, ShopOffers.Offer offer, String type) {
        super("&8Kup więcej stacków", 5);
        this.economyManager = eco;
        this.playtimeManager = pt;
        this.offer = offer;
        this.currencyType = type;
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
        String balanceLine = currencyType.equals("MONEY") ?
                "&8» &7Twój stan konta: &a" + FormatUtil.formatMoney(economyManager.getBalance(p)) + "$" :
                "&8» &7Twoje monety czasu: &e" + playtimeManager.getTimeCoins(p.getUniqueId());

        setItem(5, createGuiItem(Material.IRON_BARS, "&d&lINFORMACJE",
                "&7Wybierz interesującą cię",
                "&7ilość stacków do zakupu.",
                "",
                balanceLine));

        // Guziki od 2 do 10 stacków
        for (int i = 0; i < 9; i++) {
            int stacks = i + 2;
            int totalItems = stacks * 64;
            int totalCost = totalItems * offer.getPrice();

            String costStr = currencyType.equals("MONEY") ? FormatUtil.formatMoney(totalCost) : String.valueOf(totalCost);
            String costString = currencyType.equals("MONEY") ? "&a" + costStr + "$" : "&e" + totalCost + " Czasu";

            ItemStack item = createGuiItem(offer.getDisplayItem().getType(), "&eKup " + stacks + " stacki",
                    "&8» &aInformacje:",
                    "&8⁑ &7Otrzymasz: &ex" + totalItems,
                    "&8⁑ &7Łączny koszt: " + costString,
                    "",
                    "&8» &aKliknij LPM, aby kupić!");

            item.setAmount(stacks);
            setItem(optionSlots[i], item);
        }

        setItem(41, createGuiItem(Material.BARRIER, "&cPowrót", "&7Kliknij, aby wrócić."));
    }

    @Override
    public void handleClickAction(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        int clickedSlot = e.getSlot() + 1;

        if (clickedSlot == 41) {
            new ShopBuyGui(economyManager, playtimeManager, offer, currencyType).show(p);
            return;
        }

        for (int i = 0; i < 9; i++) {
            if (clickedSlot == optionSlots[i]) {
                int stacks = i + 2;
                buyStacks(p, stacks);
                return;
            }
        }
    }

    private void buyStacks(Player p, int stacks) {
        int amount = stacks * 64;
        int totalCost = amount * offer.getPrice();

        if (currencyType.equals("MONEY")) {
            double balance = economyManager.getBalance(p);
            if (balance < totalCost) {
                p.closeInventory();
                double missing = totalCost - balance;

                String missingFormat = FormatUtil.formatMoney(missing);

                PlayerMessage.message(p, MessageType.SUBTITLE, "<red>Brakuje ci <yellow>$" + missingFormat);
                SoundsUtil.error(p);
                return;
            }
            economyManager.setBalance(p, balance - totalCost);
        } else {
            long timeBalance = playtimeManager.getTimeCoins(p.getUniqueId());
            if (timeBalance < totalCost) {
                p.closeInventory();
                long missing = totalCost - timeBalance;

                String missingFormat = missing >= 1000 ? String.format(java.util.Locale.US, "%.1fk", missing / 1000.0) : String.valueOf(missing);

                PlayerMessage.message(p, MessageType.SUBTITLE, "<red>Brakuje ci <yellow>" + missingFormat + " <red>Czasu");
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
            for(int i = 0; i < stacks; i++) {
                ItemStack itemToGive = offer.getDisplayItem();
                itemToGive.setAmount(64);
                p.getInventory().addItem(itemToGive);
            }
        }

        PlayerMessage.message(p, MessageType.CHAT, "<green>Pomyślnie zakupiono przedmioty!");
        SoundsUtil.itemshop(p);
        p.closeInventory();
    }
}
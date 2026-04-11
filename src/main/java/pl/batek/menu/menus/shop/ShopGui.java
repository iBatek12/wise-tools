package pl.batek.menu.menus.shop;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import pl.batek.economy.EconomyManager;
import pl.batek.manager.PlaytimeManager;
import pl.batek.menu.GuiCreator;

public class ShopGui extends GuiCreator {

    private final EconomyManager economyManager;
    private final PlaytimeManager playtimeManager;

    public ShopGui(EconomyManager economyManager, PlaytimeManager playtimeManager) {
        super("&8Sklep", 5); // Menu na zdjęciu ma 5 rzędów
        this.economyManager = economyManager;
        this.playtimeManager = playtimeManager;
    }

    @Override
    public void setContents(Player p) {
        // --- DEKORACJA SZYBAMI (Wzór 1:1 z obrazka) ---
        int[] purpleGlass = {1, 9, 37, 45};
        for (int slot : purpleGlass) {
            setItem(slot, createGuiItem(Material.PURPLE_STAINED_GLASS_PANE, " "));
        }

        int[] pinkGlass = {2, 8, 10, 18, 28, 36, 38, 44};
        for (int slot : pinkGlass) {
            setItem(slot, createGuiItem(Material.PINK_STAINED_GLASS_PANE, " "));
        }

        int[] grayGlass = {3, 4, 6, 7, 39, 40, 42, 43};
        for (int slot : grayGlass) {
            setItem(slot, createGuiItem(Material.WHITE_STAINED_GLASS_PANE, " "));
        }

        // --- KATEGORIE SKLEPU (Rząd 3) ---
        setItem(21, createGuiItem(Material.EMERALD, "&a&lSKLEP ZA PIENIĄDZE",
                "&aCo znajdziesz w kategorii:", "&8» &7W tej kategorii możesz zakupić", "&8» &7różne potrzebne przedmioty", "", "&8» &aKliknij LPM, aby otworzyć!"));

        setItem(23, createGuiItem(Material.RED_DYE, "&c&lSPRZEDAŻ PRZEDMIOTÓW",
                "&cCo znajdziesz w kategorii:", "&8» &7W tej kategorii sprzedasz różne", "&8» &7przedmioty dzięki którym", "&8» &7możesz się &cwzbogacić &7na serwerze", "", "&8» &aKliknij LPM, aby otworzyć!"));

        setItem(25, createGuiItem(Material.CLOCK, "&e&lSKLEP ZA CZAS",
                "&eCo znajdziesz w kategorii:", "&8» &7W tej kategorii wydasz swoje &azdobyte", "&8» &7Monety czasu na przedmioty &bpremium", "", "&8» &aKliknij LPM, aby otworzyć!"));
    }

    @Override
    public void handleClickAction(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        int slot = e.getSlot() + 1;

        if (slot == 21) {
            new ShopCategoryGui(economyManager, playtimeManager, "MONEY").show(p);
        } else if (slot == 23) {
            new ShopSellGui(economyManager, playtimeManager).show(p);
        } else if (slot == 25) {
            new ShopCategoryGui(economyManager, playtimeManager, "TIME").show(p);
        }
    }
}
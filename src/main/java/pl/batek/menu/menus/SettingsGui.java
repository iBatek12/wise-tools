package pl.batek.menu.menus;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import pl.batek.manager.SettingsManager;
import pl.batek.menu.GuiCreator;

public class SettingsGui extends GuiCreator {

    private final SettingsManager settingsManager;

    public SettingsGui(SettingsManager settingsManager) {
        super("&8Ustawienia gracza", 3);
        this.settingsManager = settingsManager;
    }

    @Override
    public void setContents(Player p) {
        // --- Widoczność Czatu (Slot 14) ---
        boolean chatVisible = settingsManager.isChatVisible(p.getUniqueId());
        Material chatMat = chatVisible ? Material.LIME_DYE : Material.GRAY_DYE;
        String chatStatus = chatVisible ? "&aWłączony" : "&cWyłączony";

        setItem(14, createGuiItem(chatMat, "&7Widoczność czatu: " + chatStatus,
                "&7Kliknij, aby zmienić widoczność", "&7wiadomości od innych graczy."));


        boolean titleVisible = settingsManager.isTitleVisible(p.getUniqueId());
        Material titleMat = titleVisible ? Material.LIME_DYE : Material.GRAY_DYE;
        String titleStatus = titleVisible ? "&aWłączone" : "&cWyłączone";

        setItem(12, createGuiItem(titleMat, "&7Powiadomienia z eventów: " + titleStatus,
                "&7Kliknij, aby włączyć/wyłączyć napisy", "&7oraz dźwięki na ekranie (np. z KeyAll)."));
    }

    @Override
    public void handleClickAction(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        // Kliknięcie w czat (slot 14 -> index 13)
        if (e.getSlot() == 13) {
            settingsManager.toggleChat(p.getUniqueId());
            setContents(p); // Odświeżamy ikonki
        }

        // Kliknięcie w napisy (slot 12 -> index 11)
        if (e.getSlot() == 11) {
            settingsManager.toggleTitle(p.getUniqueId());
            setContents(p); // Odświeżamy ikonki
        }
    }
}
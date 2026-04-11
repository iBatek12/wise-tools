package pl.batek.controller;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.PlayerInventory;
import pl.batek.menu.GuiCreator;

public class MenuController implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Inventory inventory = e.getClickedInventory();

        if (inventory == null) {
            return;
        }

        InventoryHolder inventoryHolder = inventory.getHolder();

        if (inventoryHolder instanceof GuiCreator) {
            GuiCreator menu = (GuiCreator) inventoryHolder;
            menu.handleClickAction(e);
            e.setCancelled(true);
            return;
        }
        Inventory openInventory = e.getWhoClicked().getOpenInventory().getTopInventory();
        if (openInventory.getHolder() instanceof GuiCreator) {
            if (inventory instanceof PlayerInventory) {
                e.setCancelled(true);
            }
        }
    }
}
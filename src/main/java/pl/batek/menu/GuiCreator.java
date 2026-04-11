package pl.batek.menu;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.batek.WiseMain;
import pl.batek.util.AdventureUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class GuiCreator implements InventoryHolder {
    public static WiseMain main = WiseMain.getInstance();
    protected Inventory inv;
    private Component titleComponent;
    private int size;

    public GuiCreator(String title, int rows) {
        this.titleComponent = AdventureUtil.translate(title);
        this.size = rows * 9;
        this.inv = Bukkit.createInventory(this, this.size, this.titleComponent);
    }

    public void setItem(int i, ItemStack itemStack) {
        this.inv.setItem(i - 1, itemStack);
    }

    public Inventory getInventory() {
        return this.inv;
    }

    public void setContents(Player p) {
    }

    public ItemStack getItemStack(int slot) {
        return this.inv.getItem(slot - 1);
    }

    public void show(Player p) {
        if (this.inv != null) {
            this.setContents(p);
        }
        p.openInventory(this.inv);
    }

    public void show(Player p, String title) {
        this.titleComponent = AdventureUtil.translate(title);
        this.inv = Bukkit.createInventory(this, this.size, this.titleComponent);
        if (this.inv != null) {
            this.setContents(p);
        }
        p.openInventory(this.inv);
    }

    public Inventory getInv() {
        return this.inv;
    }

    public Component getTitleComponent() {
        return this.titleComponent;
    }

    public int getSize() {
        return this.size;
    }

    public abstract void handleClickAction(InventoryClickEvent e);
    protected ItemStack createGuiItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.displayName(AdventureUtil.translate(name));

            List<Component> loreComponents = Arrays.stream(lore)
                    .map(AdventureUtil::translate)
                    .collect(Collectors.toList());

            meta.lore(loreComponents);
            item.setItemMeta(meta);
        }
        return item;
    }
}
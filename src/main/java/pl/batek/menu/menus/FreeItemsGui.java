package pl.batek.menu.menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import pl.batek.manager.FreeItemsManager;
import pl.batek.menu.GuiCreator;
import pl.batek.util.AdventureUtil;

public class FreeItemsGui extends GuiCreator {

    private final FreeItemsManager manager;

    public FreeItemsGui(FreeItemsManager manager) {
        super("&8Darmowe Przedmioty", 3);
        this.manager = manager;
    }

    @Override
    public void setContents(Player p) {
        setItem(12, createGuiItem(Material.COBWEB, "&cZestaw Trapów", "&7Odbierz itemy do pułapek!", "&7Czas oczekiwania: &e60 sekund"));
        setItem(14, createGuiItem(Material.TRIPWIRE_HOOK, "&bZestaw Kluczy", "&7Odbierz darmowe klucze!", "&7Czas oczekiwania: &e5 godzin"));
        setItem(16, createGuiItem(Material.DIAMOND_BLOCK, "&6Zestaw Startowy", "&7Potężny zestaw do gry!", "&7Możliwy do odebrania &etylko raz&7."));
    }

    @Override
    public void handleClickAction(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        int slot = e.getSlot();

        if (slot == 11) handleTrapKit(p);
        else if (slot == 13) handleKeysKit(p);
        else if (slot == 15) handleOneTimeKit(p);

        p.closeInventory();
    }

    private void handleTrapKit(Player p) {
        long cooldown = manager.getTrapCooldown(p.getUniqueId());
        if (cooldown > 0) {
            p.sendMessage(AdventureUtil.translate("&cMusisz odczekać jeszcze &e" + (cooldown / 1000) + "s &caby odebrać ten zestaw!"));
            return;
        }

        if (!hasFreeSpace(p, 6)) {
            p.sendMessage(AdventureUtil.translate("&cMusisz mieć przynajmniej 6 wolnych miejsc w ekwipunku!"));
            return;
        }

        manager.setTrapCooldown(p.getUniqueId());

        p.getInventory().addItem(new ItemStack(Material.PAINTING, 8));
        p.getInventory().addItem(new ItemStack(Material.RAIL, 32));
        p.getInventory().addItem(new ItemStack(Material.WHITE_WOOL, 32));
        p.getInventory().addItem(new ItemStack(Material.WHITE_BANNER, 8));
        p.getInventory().addItem(new ItemStack(Material.LEVER, 24));
        p.getInventory().addItem(new ItemStack(Material.MINECART, 1));

        p.sendMessage(AdventureUtil.translate("&aPomyślnie odebrano &cZestaw Trapów&a!"));
    }

    private void handleKeysKit(Player p) {
        long cooldown = manager.getKeysCooldown(p.getUniqueId());
        if (cooldown > 0) {
            long hours = (cooldown / (1000 * 60 * 60)) % 24;
            long minutes = (cooldown / (1000 * 60)) % 60;
            p.sendMessage(AdventureUtil.translate("&cMusisz odczekać jeszcze &e" + hours + "h " + minutes + "m &caby odebrać ten zestaw!"));
            return;
        }

        manager.setKeysCooldown(p.getUniqueId());

        String nick = p.getName();
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "case give " + nick + " skarb 10");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "case give " + nick + " anarchiczna 20");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "case give " + nick + " epicka 20");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "case give " + nick + " rzadka 20");

        p.sendMessage(AdventureUtil.translate("&aPomyślnie odebrano &bZestaw Kluczy&a!"));
    }

    private void handleOneTimeKit(Player p) {
        if (manager.hasClaimedOneTime(p.getUniqueId())) {
            p.sendMessage(AdventureUtil.translate("&cJuż odebrałeś ten zestaw!"));
            return;
        }
        if (!hasFreeSpace(p, 25)) {
            p.sendMessage(AdventureUtil.translate("&cPotrzebujesz przynajmniej 25 wolnych miejsc, aby odebrać ten potężny zestaw!"));
            return;
        }

        manager.setClaimedOneTime(p.getUniqueId());
        PlayerInventory inv = p.getInventory();

        ItemStack helmet = createArmor(Material.NETHERITE_HELMET);
        ItemStack chestplate = createArmor(Material.NETHERITE_CHESTPLATE);
        ItemStack leggings = createArmor(Material.NETHERITE_LEGGINGS);
        ItemStack boots = createArmor(Material.NETHERITE_BOOTS);
        inv.setHelmet(helmet);
        inv.setChestplate(chestplate);
        inv.setLeggings(leggings);
        inv.setBoots(boots);
        inv.addItem(helmet, chestplate, leggings, boots);
        ItemStack shield = new ItemStack(Material.SHIELD);
        inv.setItemInOffHand(shield);

        ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
        addEnchant(sword, "&4Anarchiczny miecz", Enchantment.SHARPNESS, 7, Enchantment.FIRE_ASPECT, 2, Enchantment.UNBREAKING, 3);

        ItemStack pickaxe = new ItemStack(Material.NETHERITE_PICKAXE);
        addEnchant(pickaxe, "&4Anarchiczny kilof", Enchantment.FORTUNE, 5, Enchantment.UNBREAKING, 5, Enchantment.EFFICIENCY, 10);

        ItemStack knock = new ItemStack(Material.DIAMOND_SWORD);
        addEnchant(sword, "&dKnock", Enchantment.KNOCKBACK, 2);

        ItemStack axe = new ItemStack(Material.NETHERITE_AXE);
        addEnchant(axe, null, Enchantment.EFFICIENCY, 6);

        inv.addItem(sword, knock , axe, pickaxe);
        inv.addItem(new ItemStack(Material.OBSIDIAN, 64));
        inv.addItem(new ItemStack(Material.OBSIDIAN, 64));
        inv.addItem(new ItemStack(Material.OBSIDIAN, 64));
        inv.addItem(new ItemStack(Material.TOTEM_OF_UNDYING, 1));
        inv.addItem(new ItemStack(Material.TOTEM_OF_UNDYING, 1));
        inv.addItem(new ItemStack(Material.WATER_BUCKET, 1));
        inv.addItem(new ItemStack(Material.WATER_BUCKET, 1));
        inv.addItem(new ItemStack(Material.LAVA_BUCKET, 1));
        inv.addItem(new ItemStack(Material.LAVA_BUCKET, 1));
        inv.addItem(new ItemStack(Material.COBWEB, 64));
        inv.addItem(new ItemStack(Material.COBWEB, 64));
        inv.addItem(new ItemStack(Material.ARROW, 64));
        inv.addItem(new ItemStack(Material.ARROW, 64));
        inv.addItem(new ItemStack(Material.OAK_BUTTON, 64));
        inv.addItem(new ItemStack(Material.OAK_PRESSURE_PLATE, 64));
        inv.addItem(new ItemStack(Material.LEVER, 32));
        inv.addItem(new ItemStack(Material.GOLDEN_APPLE, 8));
        inv.addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 16));
        inv.addItem(new ItemStack(Material.ENDER_PEARL, 16));
        inv.addItem(new ItemStack(Material.BOW, 1));

        inv.addItem(new ItemStack(Material.EXPERIENCE_BOTTLE, 2));

        p.sendMessage(AdventureUtil.translate("&aPomyślnie odebrano potężny &6Zestaw Startowy&a!"));
    }


    private ItemStack createArmor(Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.addEnchant(Enchantment.PROTECTION, 6, true);
            meta.addEnchant(Enchantment.UNBREAKING, 6, true);
            item.setItemMeta(meta);
        }
        return item;
    }

    private void addEnchant(ItemStack item, String name, Object... enchants) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (name != null) {
                // ZMIANA: Zamiast komponentu rzutowanego na String (co ucinało kolory),
                // aplikujemy Component bezpośrednio za pomocą metody z Adventure API
                meta.displayName(AdventureUtil.translate(name));
            }
            // Pętla pobierająca pary: (Enchantment, Poziom)
            for (int i = 0; i < enchants.length; i += 2) {
                Enchantment ench = (Enchantment) enchants[i];
                int level = (int) enchants[i + 1];
                meta.addEnchant(ench, level, true);
            }
            item.setItemMeta(meta);
        }
    }

    private boolean hasFreeSpace(Player player, int requiredSlots) {
        int free = 0;
        for (ItemStack item : player.getInventory().getStorageContents()) {
            if (item == null || item.getType() == Material.AIR) {
                free++;
            }
        }
        return free >= requiredSlots;
    }
}
package pl.batek.manager;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ShopOffers {

    public static class Offer {
        private final String type;
        private final ItemStack displayItem;
        private final String displayName;
        private final int price;
        private final String command;

        public Offer(String type, ItemStack displayItem, String displayName, int price) {
            this(type, displayItem, displayName, price, null);
        }

        public Offer(String type, ItemStack displayItem, String displayName, int price, String command) {
            this.type = type;
            this.displayItem = displayItem;
            this.displayName = displayName;
            this.price = price;
            this.command = command;
        }

        public String getType() { return type; }
        public ItemStack getDisplayItem() { return displayItem.clone(); }
        public String getDisplayName() { return displayName; }
        public int getPrice() { return price; }
        public String getCommand() { return command; }
        public boolean isCommand() { return command != null && !command.isEmpty(); }
    }

    public static class SellOffer {
        private final Material material;
        private final String displayName;
        private final double sellPrice;

        public SellOffer(Material material, String displayName, double sellPrice) {
            this.material = material;
            this.displayName = displayName;
            this.sellPrice = sellPrice;
        }

        public Material getMaterial() { return material; }
        public String getDisplayName() { return displayName; }
        public double getSellPrice() { return sellPrice; }
    }

    private static final List<Offer> buyOffers = new ArrayList<>();
    private static final List<SellOffer> sellOffers = new ArrayList<>();

    static {
        buyOffers.add(new Offer("MONEY", new ItemStack(Material.FIREWORK_ROCKET), "&fFajerwerki", 300));
        buyOffers.add(new Offer("TIME", new ItemStack(Material.DIAMOND), "&bDiamenty", 1000));
        buyOffers.add(new Offer("TIME", new ItemStack(Material.TRIPWIRE_HOOK), "&aKlucz do Skrzyni", 2000, "case give {player} epicka 1"));

        // === OFERTY SPRZEDAŻY (1:1 ze screenami) ===
        sellOffers.add(new SellOffer(Material.RAW_COPPER, "&fRaw Copper", 0.3));
        sellOffers.add(new SellOffer(Material.DIAMOND, "&fDiamond", 5.0));
        sellOffers.add(new SellOffer(Material.RAW_IRON, "&fRaw Iron", 0.1));
    }

    public static List<Offer> getBuyOffers(String type) {
        return buyOffers.stream().filter(o -> o.getType().equals(type)).collect(Collectors.toList());
    }

    public static List<SellOffer> getSellOffers() {
        return sellOffers;
    }
}
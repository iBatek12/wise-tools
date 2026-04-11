package pl.batek.economy;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import pl.batek.util.FormatUtil;

import java.util.List;

public class VaultHook implements Economy {

    private final JavaPlugin plugin;
    private final EconomyManager ecoManager;

    public VaultHook(JavaPlugin plugin, EconomyManager ecoManager) {
        this.plugin = plugin;
        this.ecoManager = ecoManager;
    }

    @Override
    public boolean isEnabled() { return plugin != null && plugin.isEnabled(); }

    @Override
    public String getName() { return "WiseEconomy"; }

    @Override
    public boolean hasBankSupport() { return false; }

    @Override
    public int fractionalDigits() { return 2; }

    @Override
    public String format(double amount) {
        return pl.batek.util.FormatUtil.formatMoney(amount) + "$";
    }

    @Override
    public String currencyNamePlural() { return "Dolarow"; }

    @Override
    public String currencyNameSingular() { return "Dolar"; }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return ecoManager.hasAccount(player);
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return ecoManager.getBalance(player);
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return getBalance(player) >= amount;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        if (amount < 0) return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Kwota nie moze byc ujemna");
        if (!has(player, amount)) return new EconomyResponse(0, getBalance(player), EconomyResponse.ResponseType.FAILURE, "Brak srodkow");

        double newBalance = getBalance(player) - amount;
        ecoManager.setBalance(player, newBalance);
        return new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        if (amount < 0) return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Kwota nie moze byc ujemna");

        double newBalance = getBalance(player) + amount;
        ecoManager.setBalance(player, newBalance);
        return new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        if (hasAccount(player)) return false;
        ecoManager.setBalance(player, 0.0);
        return true;
    }

    // --- METODY DEPRECATED (Stare API Vaulta) ---
    @Override public boolean hasAccount(String playerName) { return false; }
    @Override public double getBalance(String playerName) { return 0; }
    @Override public boolean has(String playerName, double amount) { return false; }
    @Override public EconomyResponse withdrawPlayer(String playerName, double amount) { return null; }
    @Override public EconomyResponse depositPlayer(String playerName, double amount) { return null; }
    @Override public boolean createPlayerAccount(String playerName) { return false; }

    // --- METODY BANKOWE ---
    @Override public boolean hasAccount(String playerName, String worldName) { return false; }
    @Override public double getBalance(String playerName, String worldName) { return 0; }
    @Override public boolean has(String playerName, String worldName, double amount) { return false; }
    @Override public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) { return null; }
    @Override public EconomyResponse depositPlayer(String playerName, String worldName, double amount) { return null; }
    @Override public boolean createPlayerAccount(String playerName, String worldName) { return false; }
    @Override public boolean hasAccount(OfflinePlayer player, String worldName) { return hasAccount(player); }
    @Override public double getBalance(OfflinePlayer player, String worldName) { return getBalance(player); }
    @Override public boolean has(OfflinePlayer player, String worldName, double amount) { return has(player, amount); }
    @Override public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) { return withdrawPlayer(player, amount); }
    @Override public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) { return depositPlayer(player, amount); }
    @Override public boolean createPlayerAccount(OfflinePlayer player, String worldName) { return createPlayerAccount(player); }
    @Override public EconomyResponse createBank(String name, String player) { return null; }
    @Override public EconomyResponse createBank(String name, OfflinePlayer player) { return null; }
    @Override public EconomyResponse deleteBank(String name) { return null; }
    @Override public EconomyResponse bankBalance(String name) { return null; }
    @Override public EconomyResponse bankHas(String name, double amount) { return null; }
    @Override public EconomyResponse bankWithdraw(String name, double amount) { return null; }
    @Override public EconomyResponse bankDeposit(String name, double amount) { return null; }
    @Override public EconomyResponse isBankOwner(String name, String playerName) { return null; }
    @Override public EconomyResponse isBankOwner(String name, OfflinePlayer player) { return null; }
    @Override public EconomyResponse isBankMember(String name, String playerName) { return null; }
    @Override public EconomyResponse isBankMember(String name, OfflinePlayer player) { return null; }
    @Override public List<String> getBanks() { return null; }
}
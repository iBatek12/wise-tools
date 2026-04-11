package pl.batek.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.batek.manager.TopManager;
import pl.batek.manager.VanishManager;
import pl.batek.util.AdventureUtil;
import pl.batek.util.FormatUtil;

public class WisePlaceholder extends PlaceholderExpansion {

    private final VanishManager vanishManager;
    private final TopManager topManager;

    public WisePlaceholder(VanishManager vanishManager, TopManager topManager) {
        this.vanishManager = vanishManager;
        this.topManager = topManager;
    }

    @Override
    public @NotNull String getIdentifier() { return "wise"; }

    @Override
    public @NotNull String getAuthor() { return "Batek"; }

    @Override
    public @NotNull String getVersion() { return "1.0.0"; }

    @Override
    public boolean persist() { return true; }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {

        if (params.equalsIgnoreCase("vanish")) {
            if (player != null && vanishManager.isVanished(player)) {
                Component vanishComp = AdventureUtil.miniMessage("<red>[V] ", null);
                return AdventureUtil.componentToString(vanishComp);
            }
            return "";
        }

        if (params.startsWith("top_money_name_")) {
            try {
                int pos = Integer.parseInt(params.replace("top_money_name_", ""));
                return topManager.getTopMoney(pos).name();
            } catch (Exception ignored) { return "Błąd"; }
        }

        if (params.startsWith("top_money_value_")) {
            try {
                int pos = Integer.parseInt(params.replace("top_money_value_", ""));
                double value = topManager.getTopMoney(pos).value();
                // ODPALA FORMAT Z K/M/B
                Component moneyComp = AdventureUtil.miniMessage("<yellow>" + FormatUtil.formatMoney(value) + "$", null);
                return AdventureUtil.componentToString(moneyComp);
            } catch (Exception ignored) { return "0$"; }
        }

        if (params.startsWith("top_time_name_")) {
            try {
                int pos = Integer.parseInt(params.replace("top_time_name_", ""));
                return topManager.getTopTime(pos).name();
            } catch (Exception ignored) { return "Błąd"; }
        }

        if (params.startsWith("top_time_value_")) {
            try {
                int pos = Integer.parseInt(params.replace("top_time_value_", ""));
                String formattedTime = FormatUtil.formatTime((int) topManager.getTopTime(pos).value());
                Component timeComp = AdventureUtil.miniMessage("<white>" + formattedTime, null);
                return AdventureUtil.componentToString(timeComp);
            } catch (Exception ignored) { return "0sek"; }
        }

        return null;
    }
}
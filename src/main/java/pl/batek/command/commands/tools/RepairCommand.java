package pl.batek.command.commands.tools;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import pl.batek.message.MessageType;
import pl.batek.message.PlayerMessage;
import pl.batek.util.SoundsUtil;

@Command(name = "repair", aliases = {"napraw"})
public class RepairCommand {

    // --- /repair ---
    @Execute
    @Permission("core.repair")
    void repairHand(@Context Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null || item.getType() == Material.AIR) {
            PlayerMessage.message(player, MessageType.CHAT, "<red>Musisz trzymać przedmiot w ręce!");
            SoundsUtil.error(player);
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof Damageable)) {
            PlayerMessage.message(player, MessageType.CHAT, "<red>Tego przedmiotu nie da się naprawić!");
            SoundsUtil.error(player);
            return;
        }

        Damageable damageable = (Damageable) meta;
        if (!damageable.hasDamage()) { // Sprawdzamy czy w ogóle jest zepsuty
            PlayerMessage.message(player, MessageType.CHAT, "<red>Ten przedmiot jest już w pełni naprawiony!");
            SoundsUtil.error(player);
            return;
        }

        damageable.setDamage(0); // Ustawiamy uszkodzenia na 0
        item.setItemMeta(meta);

        PlayerMessage.message(player, MessageType.CHAT, "<green>Pomyślnie naprawiono przedmiot w ręce!");
        SoundsUtil.accept(player);
    }

    // --- /repair all ---
    @Execute(name = "all")
    @Permission("core.repair.all")
    void repairAll(@Context Player player) {
        boolean repairedAny = false;

        // Pętla sprawdzająca każdy slot w ekwipunku gracza (w tym zbroję)
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR) continue;

            ItemMeta meta = item.getItemMeta();
            if (meta instanceof Damageable) {
                Damageable damageable = (Damageable) meta;
                if (damageable.hasDamage()) {
                    damageable.setDamage(0);
                    item.setItemMeta(meta);
                    repairedAny = true; // Flaga, że cokolwiek zostało naprawione
                }
            }
        }

        if (repairedAny) {
            PlayerMessage.message(player, MessageType.CHAT, "<green>Pomyślnie naprawiono wszystkie przedmioty w ekwipunku!");
            SoundsUtil.accept(player);
        } else {
            PlayerMessage.message(player, MessageType.CHAT, "<red>Nie miałeś żadnych zepsutych przedmiotów w ekwipunku!");
            SoundsUtil.error(player);
        }
    }
}
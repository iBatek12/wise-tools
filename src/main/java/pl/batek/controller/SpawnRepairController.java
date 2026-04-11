package pl.batek.controller;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import pl.batek.message.MessageType;
import pl.batek.message.PlayerMessage;
import pl.batek.util.SoundsUtil;

public class SpawnRepairController implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        // Sprawdzamy, czy gracz kliknął Prawym Przyciskiem Myszy na blok
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block clickedBlock = event.getClickedBlock();
        // Sprawdzamy, czy blok to REINFORCED_DEEPSLATE
        if (clickedBlock == null || clickedBlock.getType() != Material.REINFORCED_DEEPSLATE) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        // Sprawdzamy, czy gracz trzyma coś w ręce
        if (item == null || item.getType() == Material.AIR) return;

        // --- SPRAWDZANIE REGIONU WORLDGUARD ---
        boolean inSpawn = false;
        try {
            com.sk89q.worldedit.util.Location weLoc = BukkitAdapter.adapt(clickedBlock.getLocation());
            ApplicableRegionSet set = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().getApplicableRegions(weLoc);
            for (ProtectedRegion region : set) {
                if (region.getId().equalsIgnoreCase("spawn")) {
                    inSpawn = true;
                    break;
                }
            }
        } catch (Exception ignored) {}

        // Jeśli kliknięty blok nie znajduje się w regionie "spawn", przerywamy
        if (!inSpawn) return;

        // --- LOGIKA NAPRAWY ---
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof Damageable)) {
            PlayerMessage.message(player, MessageType.CHAT, "<red>Tego przedmiotu nie da się naprawić!");
            SoundsUtil.error(player);
            return;
        }

        Damageable damageable = (Damageable) meta;
        if (!damageable.hasDamage()) {
            PlayerMessage.message(player, MessageType.CHAT, "<red>Ten przedmiot jest już w pełni naprawiony!");
            SoundsUtil.error(player);
            return;
        }

        // Naprawa i aktualizacja przedmiotu
        damageable.setDamage(0);
        item.setItemMeta(meta);

        // Zablokowanie eventu (zapobiega to ewentualnym interakcjom z blokiem)
        event.setCancelled(true);

        PlayerMessage.message(player, MessageType.CHAT, "<green>Pomyślnie naprawiono przedmiot za pomocą kowadła na spawnie!");
        SoundsUtil.orb(player);
    }
}
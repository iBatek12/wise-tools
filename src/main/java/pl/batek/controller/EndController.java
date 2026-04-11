package pl.batek.controller;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import pl.batek.manager.EndManager;
import pl.batek.util.AdventureUtil;

public class EndController implements Listener {

    private final EndManager endManager;

    public EndController(EndManager endManager) {
        this.endManager = endManager;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlock().getWorld().getEnvironment() != World.Environment.THE_END) return;

        Material type = event.getBlock().getType();

        if (type == Material.OBSIDIAN || type == Material.OAK_PLANKS) {
            endManager.addPlacedBlock(event.getBlock());
        } else {
            event.setCancelled(true);
            event.getPlayer().sendMessage(AdventureUtil.translate("&cW Endzie możesz stawiać tylko Obsydian i Dębowe Deski!"));
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getWorld().getEnvironment() != World.Environment.THE_END) return;

        if (endManager.isPlayerPlaced(event.getBlock())) {
            endManager.removePlacedBlock(event.getBlock());
        } else {
            event.setCancelled(true);
            event.getPlayer().sendMessage(AdventureUtil.translate("&cW Endzie możesz niszczyć tylko bloki postawione przez graczy!"));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCrystalPlace(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getItem() != null && event.getItem().getType() == Material.END_CRYSTAL) {
                if (event.getPlayer().getWorld().getEnvironment() != World.Environment.THE_END) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(AdventureUtil.translate("&cKryształy możesz stawiać tylko w Endzie!"));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        if (event.getEntity() instanceof EnderCrystal) {
            event.setFire(false);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity() instanceof EnderCrystal) {
            event.blockList().clear();

            Block block = event.getLocation().getBlock();
            if (block.getType() == Material.FIRE) {
                block.setType(Material.AIR);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (event.getCause() == BlockIgniteEvent.IgniteCause.ENDER_CRYSTAL || event.getCause() == BlockIgniteEvent.IgniteCause.EXPLOSION) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Material bucket = event.getBucket();

        if (bucket == Material.WATER_BUCKET || bucket == Material.LAVA_BUCKET) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(AdventureUtil.translate("&cNie możesz wylewać cieczy!"));
        }
    }
}
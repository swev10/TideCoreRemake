package com.tideCore.managers;

import com.tideCore.TideCore;
import com.tideCore.gui.UpgradeGUI;
import com.tideCore.items.HarvesterFactory;
import com.tideCore.util.TideCoreKeys;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class HologramManager implements Listener {

    private static final Set<Location> hologramLocations = new HashSet<>();

    public static void scanAndCreateHolograms() {
        World world = Bukkit.getWorld("world");
        if (world == null) return;

        for (Chunk chunk : world.getLoadedChunks()) {
            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < world.getMaxHeight(); y++) {
                    for (int z = 0; z < 16; z++) {
                        Block block = chunk.getBlock(x, y, z);
                        if (block.getType() == Material.SMITHING_TABLE) {
                            Location loc = block.getLocation().add(0.5, 2, 0.5);
                            spawnHologram(loc);
                        }
                    }
                }
            }
        }
    }


    private static void spawnHologram(Location loc) {
        ArmorStand textLine1 = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        textLine1.setVisible(false);
        textLine1.setCustomName("§b§lUPGRADE STATION");
        textLine1.setCustomNameVisible(true);
        textLine1.setGravity(false);
        textLine1.setMarker(true);
        textLine1.setMetadata("upgrade_station", new FixedMetadataValue(TideCore.getInstance(), true));

        ArmorStand textLine2 = (ArmorStand) loc.clone().add(0, -0.3, 0).getWorld().spawnEntity(loc.clone().add(0, -0.3, 0), EntityType.ARMOR_STAND);
        textLine2.setVisible(false);
        textLine2.setCustomName("§7Farming Tools");
        textLine2.setCustomNameVisible(true);
        textLine2.setGravity(false);
        textLine2.setMarker(true);
        textLine2.setMetadata("upgrade_station", new FixedMetadataValue(TideCore.getInstance(), true));

        hologramLocations.add(loc);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = (Player) e.getPlayer();
        if (e.getClickedBlock() == null) return;
        if (e.getClickedBlock().getType() != Material.SMITHING_TABLE) return;

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() != Material.WOODEN_HOE) {
            player.sendMessage("§cYou need to hold your Harvester Hoe to use the station!");
            return;
        }

        UpgradeGUI.open(player);
    }
}

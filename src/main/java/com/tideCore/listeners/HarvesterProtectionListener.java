package com.tideCore.listeners;

import com.tideCore.util.TideCoreKeys;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class HarvesterProtectionListener implements Listener {

    private boolean isHarvesterHoe(ItemStack item) {
        if (item == null || item.getType() != Material.WOODEN_HOE) return false;
        if (!item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(TideCoreKeys.HARVESTER_KEY, PersistentDataType.INTEGER);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();
        if (isHarvesterHoe(item)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cYou cannot drop your Harvester Hoe!");
        }
    }

    @EventHandler
    public void onInventoryMove(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack current = event.getCurrentItem();
        ItemStack cursor = event.getCursor();

        if (event.getClickedInventory() == null) return;

        boolean isMovingHoe = isHarvesterHoe(current) || isHarvesterHoe(cursor);

        if (event.getClickedInventory().getType() == InventoryType.PLAYER) {
            return;
        }

        if (isMovingHoe) {
            event.setCancelled(true);
            player.sendMessage("§cYou cannot move your Harvester Hoe into other inventories!");
        }
    }
}

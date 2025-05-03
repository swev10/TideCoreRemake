package com.tideCore.holograms;

import com.tideCore.gui.UpgradeGUI;
import com.tideCore.util.MessageUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class UpgradeStationManager implements Listener {

    @EventHandler
    public void onSmithingInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType() != Material.SMITHING_TABLE) return;

        event.setCancelled(true);
        Player player = event.getPlayer();

        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand == null || hand.getType() != Material.WOODEN_HOE) {
            player.sendMessage(MessageUtils.prefix() + "§cYou must be holding your Harvester Hoe to use this station.");
            return;
        }

        UpgradeGUI.open(player);
    }

    public static void loadStations() {
    }

    public static void clearAll() {
    }
}

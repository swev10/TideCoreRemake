package com.tideCore.listeners;

import com.tideCore.gui.UpgradeGUI;
import com.tideCore.gui.UpgradeSelectorGUI;
import com.tideCore.util.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class UpgradeGUIListener implements Listener {

    @EventHandler
    public void onUpgradeMenuClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        Inventory inv = event.getInventory();

        if (!event.getView().getTitle().equals(UpgradeGUI.GUI_TITLE)) return;

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta() || !clicked.getItemMeta().hasLore()) return;

        String internalName = null;
        for (String loreLine : clicked.getItemMeta().getLore()) {
            if (loreLine.contains("Code:")) {
                internalName = MessageUtils.stripColor(loreLine).split(": ")[1];
                break;
            }
        }

        if (internalName != null) {
            UpgradeSelectorGUI.open(player, internalName);
        }
    }
}

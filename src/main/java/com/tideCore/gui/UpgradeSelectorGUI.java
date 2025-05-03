package com.tideCore.gui;

import com.tideCore.managers.PlayerDataManager;
import com.tideCore.util.EnchantCosts;
import com.tideCore.util.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class UpgradeSelectorGUI implements Listener {

    private static final String GUI_TITLE = "§b§lUpgrade Amounts";

    public static void open(Player player, String enchant) {
        Inventory gui = Bukkit.createInventory(null, 27, GUI_TITLE);

        ItemStack upgrade1 = createButton("§a+1 Level", 1);
        ItemStack upgrade5 = createButton("§a+5 Levels", 5);
        ItemStack upgrade10 = createButton("§a+10 Levels", 10);

        gui.setItem(11, upgrade1);
        gui.setItem(13, upgrade5);
        gui.setItem(15, upgrade10);

        player.openInventory(gui);
        UpgradeGUI.setSelectedEnchant(player.getUniqueId(), enchant);
    }

    private static ItemStack createButton(String name, int levels) {
        ItemStack item = new ItemStack(Material.LIME_DYE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(List.of(
                "§7Upgrade your enchant by §f" + levels + " §7levels!"
        ));
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (!e.getView().getTitle().equals(GUI_TITLE)) return;

        e.setCancelled(true);

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta() || !clicked.getItemMeta().hasDisplayName()) return;

        String displayName = clicked.getItemMeta().getDisplayName();
        int amount = displayName.contains("+1") ? 1 : displayName.contains("+5") ? 5 : displayName.contains("+10") ? 10 : 0;
        if (amount == 0) return;

        String enchant = UpgradeGUI.getSelectedEnchant(player.getUniqueId());
        if (enchant == null) {
            player.sendMessage(MessageUtils.prefix() + "§cNo enchant selected.");
            player.closeInventory();
            return;
        }

        int currentLevel = PlayerDataManager.getHoeEnchant(player, enchant);
        int pricePerLevel = EnchantCosts.getCost(enchant, currentLevel);
        int totalCost = pricePerLevel * amount;

        if (PlayerDataManager.getTokens(player) < totalCost) {
            player.sendMessage(MessageUtils.prefix() + "§cYou don't have enough tokens.");
            player.closeInventory();
            return;
        }

        PlayerDataManager.takeTokens(player, totalCost);
        PlayerDataManager.setHoeEnchant(player, enchant, currentLevel + amount);

        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.5f);
        player.sendMessage(MessageUtils.prefix() + "§aSuccessfully upgraded §f" + enchant + " §aby §f" + amount + " §alevels!");

        player.closeInventory();
    }
}

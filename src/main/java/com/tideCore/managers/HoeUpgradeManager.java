package com.tideCore.managers;

import com.tideCore.TideCore;
import com.tideCore.util.TideCoreKeys;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.text.NumberFormat;
import java.util.Locale;

public class HoeUpgradeManager {

    public static int getLevel(Player player, String enchant) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() != Material.WOODEN_HOE) return 0;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return 0;

        return meta.getPersistentDataContainer().getOrDefault(
                TideCoreKeys.getUpgradeKey(enchant), PersistentDataType.INTEGER, 0);
    }

    public static int getUpgradeCost(int currentLevel) {
        return 100 * (int) Math.pow(1.5, currentLevel);
    }

    public static void tryUpgrade(Player player, String enchant, int amount) {
        int tokens = PlayerDataManager.getTokens(player);
        int currentLevel = getLevel(player, enchant);
        int totalCost = 0;
        int tempLevel = currentLevel;

        for (int i = 0; i < amount; i++) {
            totalCost += getUpgradeCost(tempLevel);
            tempLevel++;
        }

        if (tokens < totalCost) {
            player.sendMessage("§cYou don't have enough tokens! Need §e" + format(totalCost) + " Tokens.");
            player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f);
            return;
        }

        PlayerDataManager.takeTokens(player, totalCost);
        setLevel(player, enchant, currentLevel + amount);

        player.sendMessage("§aSuccessfully upgraded §b" + enchant + " §ax" + amount + " levels!");
        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1f, 1.5f);

        player.closeInventory();
    }

    public static void setLevel(Player player, String enchant, int level) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() != Material.WOODEN_HOE) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        meta.getPersistentDataContainer().set(
                TideCoreKeys.getUpgradeKey(enchant), PersistentDataType.INTEGER, level);

        item.setItemMeta(meta);
    }

    private static String format(int number) {
        return NumberFormat.getNumberInstance(Locale.US).format(number);
    }
}

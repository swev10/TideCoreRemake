package com.tideCore.managers;

import com.tideCore.TideCore;
import com.tideCore.util.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.text.NumberFormat;

public class PrestigeManager {

    private static final FileConfiguration config = TideCore.getInstance().getConfig();

    public static boolean canPrestige(Player player) {
        int level = PlayerDataManager.getLevel(player);
        int prestige = PlayerDataManager.getPrestige(player);
        int cap = config.getInt("prestige.max", 10);

        if (prestige >= cap) return false;

        int requiredLevel = 10 + (prestige * 5);
        if (level < requiredLevel) return false;

        double requiredMoney = getPrestigeCost(prestige);
        return TideCore.getEconomy().has(player, requiredMoney);
    }

    public static double getPrestigeCost(int prestige) {
        double base = config.getDouble("prestige.base-cost", 50000);
        return base * Math.pow(prestige + 1, 1.5);
    }

    public static void prestige(Player player) {
        int prestige = PlayerDataManager.getPrestige(player);
        double cost = getPrestigeCost(prestige);

        if (!TideCore.getEconomy().withdrawPlayer(player, cost).transactionSuccess()) {
            player.sendMessage(MessageUtils.prefix() + "&cYou don't have enough money.");
            return;
        }

        PlayerDataManager.setLevel(player, 1);
        PlayerDataManager.setXP(player, 0);
        PlayerDataManager.setPrestige(player, prestige + 1);

        player.sendTitle("§b§lPRESTIGED!", "§fYou're now §ePrestige " + (prestige + 1), 10, 60, 10);
        player.sendMessage(MessageUtils.prefix() + "&aYou've prestiged to &bPrestige " + (prestige + 1) + "&a!");

        if (config.getBoolean("prestige.broadcast", true)) {
            Bukkit.broadcastMessage(MessageUtils.prefix() +
                    "§b" + player.getName() + " §ahas prestiged to §ePrestige " + (prestige + 1) + "§a!");
        }

        reward(player, prestige + 1);
    }

    private static void reward(Player player, int prestigeLevel) {
    }

    public static String getFormattedCost(int prestigeLevel) {
        return NumberFormat.getNumberInstance().format(getPrestigeCost(prestigeLevel));
    }
}

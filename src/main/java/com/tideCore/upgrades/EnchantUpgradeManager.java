package com.tideCore.upgrades;

import com.tideCore.managers.PlayerDataManager;
import com.tideCore.util.MessageUtils;
import org.bukkit.entity.Player;

public class EnchantUpgradeManager {

    public static void attemptUpgrade(Player player, String enchant, int levels) {
        int prestige = PlayerDataManager.getPrestige(player);
        int current = PlayerDataManager.getHoeEnchant(player, enchant);

        if (!meetsPrestigeRequirement(enchant, prestige)) {
            player.sendMessage(MessageUtils.prefix() + "§cYou don't meet the prestige requirement for this enchant.");
            return;
        }

        int costPerLevel = getCostPerLevel(enchant);
        int totalCost = costPerLevel * levels;

        if (PlayerDataManager.getTokens(player) < totalCost) {
            player.sendMessage(MessageUtils.prefix() + "§cYou don't have enough tokens.");
            return;
        }

        PlayerDataManager.takeTokens(player, totalCost);
        PlayerDataManager.setHoeEnchant(player, enchant, current + levels);

        player.sendMessage(MessageUtils.prefix() + "§aUpgraded §f" + formatEnchant(enchant) + " §aby §e+" + levels + " §alevels!");
    }

    private static boolean meetsPrestigeRequirement(String enchant, int prestige) {
        return switch (enchant.toLowerCase()) {
            case "tokenfinder", "moneyfinder" -> prestige >= 3;
            case "shardinstinct", "burst" -> prestige >= 5;
            case "flyingtokens", "flyingexp" -> prestige >= 10;
            case "expfinder" -> true;
            default -> true;
        };
    }

    private static int getCostPerLevel(String enchant) {
        return switch (enchant.toLowerCase()) {
            case "tokenfinder" -> 500;
            case "moneyfinder" -> 500;
            case "expfinder" -> 250;
            case "shardinstinct" -> 1000;
            case "burst" -> 1500;
            case "flyingtokens" -> 5000;
            case "flyingexp" -> 5000;
            default -> 1000;
        };
    }

    private static String formatEnchant(String enchant) {
        return switch (enchant.toLowerCase()) {
            case "tokenfinder" -> "Token Finder";
            case "moneyfinder" -> "Money Finder";
            case "expfinder" -> "Exp Finder";
            case "shardinstinct" -> "Shard Instinct";
            case "burst" -> "Burst";
            case "flyingtokens" -> "Flying Tokens";
            case "flyingexp" -> "Flying EXP";
            default -> enchant;
        };
    }
}

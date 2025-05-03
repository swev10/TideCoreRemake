package com.tideCore.util;

import com.tideCore.managers.PlayerDataManager;
import org.bukkit.entity.Player;

public class XPUtils {

    public static String getProgressBar(Player player) {
        int level = PlayerDataManager.getLevel(player);
        int currentXP = PlayerDataManager.getXP(player);
        int requiredXP = PlayerDataManager.xpNeeded(level);

        double progress = Math.min(1.0, currentXP / (double) requiredXP);
        int totalBars = 20;
        int filled = (int) Math.round(progress * totalBars);

        String color = MessageUtils.prestigeColor(PlayerDataManager.getPrestige(player));
        StringBuilder bar = new StringBuilder();

        for (int i = 0; i < totalBars; i++) {
            bar.append(i < filled ? color + "|" : "§7|");
        }

        return bar.toString();
    }
}

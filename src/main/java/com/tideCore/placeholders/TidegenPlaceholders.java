package com.tideCore.placeholders;

import com.tideCore.managers.PlayerDataManager;
import com.tideCore.util.MessageUtils;
import com.tideCore.util.NumberFormatter;
import com.tideCore.util.XPUtils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TidegenPlaceholders extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "tidegen";
    }

    @Override
    public @NotNull String getAuthor() {
        return "TideCore";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) return "";

        return switch (identifier.toLowerCase()) {
            case "tokens" -> NumberFormatter.format(PlayerDataManager.getTokens(player));
            case "pearls" -> NumberFormatter.format(PlayerDataManager.getPearls(player));
            case "xp" -> String.valueOf(PlayerDataManager.getXP(player));
            case "level" -> String.valueOf(PlayerDataManager.getLevel(player));
            case "level_colored" -> MessageUtils.prestigeColor(PlayerDataManager.getPrestige(player)) + PlayerDataManager.getLevel(player);
            case "xpbar" -> XPUtils.getProgressBar(player);
            case "prestige" -> String.valueOf(PlayerDataManager.getPrestige(player));
            case "prestige_colored" -> MessageUtils.prestigeColor(PlayerDataManager.getPrestige(player)) + PlayerDataManager.getPrestige(player);
            default -> null;
        };
    }
}

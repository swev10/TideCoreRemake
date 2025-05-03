package com.tideCore.util;

import com.tideCore.TideCore;
import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtils {

    private static String prefix = "&8[&#7BB8E9&lTide&#9ECFF6&lGen&8] &f• ";

    public static String prefix() {
        return color(prefix);
    }

    public static void reloadPrefix() {
        prefix = TideCore.getInstance().getConfig().getString("prefix", prefix);
    }

    public static String color(String message) {
        Pattern pattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
        Matcher matcher = pattern.matcher(message);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String hex = matcher.group(1);
            StringBuilder replacement = new StringBuilder("§x");
            for (char c : hex.toCharArray()) {
                replacement.append('§').append(c);
            }
            matcher.appendReplacement(buffer, replacement.toString());
        }

        matcher.appendTail(buffer);
        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }

    public static String prestigeColor(int prestige) {
        if (prestige >= 21) return "§6";
        if (prestige >= 11) return "§c";
        return "§a";
    }

    public static String stripColor(String input) {
        return ChatColor.stripColor(color(input));
    }
}

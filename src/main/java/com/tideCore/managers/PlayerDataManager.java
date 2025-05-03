package com.tideCore.managers;

import com.tideCore.TideCore;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class PlayerDataManager {

    public static File getFile(Player player) {
        return new File(TideCore.getInstance().getDataFolder(), "players/" + player.getUniqueId() + ".yml");
    }

    public static FileConfiguration get(Player player) {
        File file = getFile(player);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public static void save(Player player, FileConfiguration config) {
        try {
            config.save(getFile(player));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getXP(Player player) {
        return get(player).getInt("xp", 0);
    }

    public static int getLevel(Player player) {
        return get(player).getInt("level", 1);
    }

    public static int getPrestige(Player player) {
        return get(player).getInt("prestige", 0);
    }

    public static int getTokens(Player player) {
        return get(player).getInt("tokens", 0);
    }

    public static int getPearls(Player player) {
        return get(player).getInt("pearls", 0);
    }

    public static void addLevel(Player player, int amount) {
        setLevel(player, getLevel(player) + amount);
    }

    public static void takeLevel(Player player, int amount) {
        setLevel(player, getLevel(player) - amount);
    }

    public static void addXP(Player player, int amount) {
        FileConfiguration config = get(player);
        int currentXP = config.getInt("xp", 0);
        int level = config.getInt("level", 1);
        int newXP = currentXP + amount;

        while (newXP >= xpNeeded(level)) {
            newXP -= xpNeeded(level);
            level++;
        }

        config.set("xp", newXP);
        config.set("level", level);
        save(player, config);
    }

    public static void addTokens(Player player, int amount) {
        setTokens(player, getTokens(player) + amount);
    }

    public static void takeTokens(Player player, int amount) {
        setTokens(player, getTokens(player) - amount);
    }

    public static void addHoeEnchant(Player player, String enchant, int levels) {
        int current = getHoeEnchant(player, enchant);
        setHoeEnchant(player, enchant, current + levels);
    }

    public static void addPearls(Player player, int amount) {
        setPearls(player, getPearls(player) + amount);
    }

    public static void takePearls(Player player, int amount) {
        setPearls(player, getPearls(player) - amount);
    }

    public static void setTokens(Player player, int amount) {
        FileConfiguration config = get(player);
        config.set("tokens", Math.max(0, amount));
        save(player, config);
    }

    public static void setPearls(Player player, int amount) {
        FileConfiguration config = get(player);
        config.set("pearls", Math.max(0, amount));
        save(player, config);
    }

    public static void setXP(Player player, int xp) {
        FileConfiguration config = get(player);
        config.set("xp", Math.max(0, xp));
        save(player, config);
    }

    public static void setLevel(Player player, int level) {
        FileConfiguration config = get(player);
        config.set("level", Math.max(1, level));
        save(player, config);
    }

    public static void setPrestige(Player player, int prestige) {
        FileConfiguration config = get(player);
        config.set("prestige", Math.max(0, prestige));
        save(player, config);
    }

    public static int getHoeEnchant(Player player, String enchant) {
        return get(player).getInt("hoe." + enchant, 0);
    }

    public static void setHoeEnchant(Player player, String enchant, int level) {
        FileConfiguration config = get(player);
        config.set("hoe." + enchant, Math.max(0, level));
        save(player, config);
    }


    public static String getSelectedEnchant(Player player) {
        return get(player).getString("selectedEnchant", "");
    }

    public static void setSelectedEnchant(Player player, String enchant) {
        FileConfiguration config = get(player);
        config.set("selectedEnchant", enchant);
        save(player, config);
    }

    public static int xpNeeded(int level) {
        return 100 + (level * level * 10);
    }
}

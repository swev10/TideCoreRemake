package com.tideCore.items;

import com.tideCore.TideCore;
import com.tideCore.managers.PlayerDataManager;
import com.tideCore.util.MessageUtils;
import com.tideCore.util.TideCoreKeys;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class HarvesterFactory {

    public static ItemStack createHarvesterHoe(Player player) {
        ItemStack hoe = new ItemStack(Material.WOODEN_HOE);
        ItemMeta meta = hoe.getItemMeta();

        meta.setDisplayName(MessageUtils.color("&#ABDAFAFarming Hoe"));

        List<String> lore = new ArrayList<>();
        lore.add(MessageUtils.color("&#7F7F7F● Farming Tool"));
        lore.add("");

        lore.add(MessageUtils.color("&#ABDAFA▎ &fMultipliers"));
        lore.add(MessageUtils.color("&#ABDAFA▎ &7Star Multiplier: &fx1"));
        lore.add(MessageUtils.color("&#ABDAFA▎ &7Money Multiplier: &fx1"));
        lore.add(MessageUtils.color("&#ABDAFA▎ &7Exp Multiplier: &fx1"));
        lore.add("");

        lore.add(MessageUtils.color("&#ABDAFA▎ &fEnchantments"));
        List<String> enchants = getEnchantLore(player);
        if (enchants.isEmpty()) {
            lore.add(MessageUtils.color("&#7F7F7FNone unlocked yet."));
        } else {
            lore.addAll(enchants);
        }

        lore.add("");
        lore.add(MessageUtils.color("&#ABDAFA➥ &7Right-click to upgrade!"));

        meta.setLore(lore);

        meta.setUnbreakable(true);
        meta.getPersistentDataContainer().set(
                TideCoreKeys.HARVESTER_KEY,
                PersistentDataType.INTEGER,
                1
        );

        hoe.setItemMeta(meta);
        return hoe;
    }

    public static List<String> getEnchantLore(Player player) {
        List<String> enchants = new ArrayList<>();

        int tokenFinder = PlayerDataManager.getHoeEnchant(player, "tokenfinder");
        if (tokenFinder > 0) enchants.add(MessageUtils.color("&#ABDAFA▎ &fToken Finder: &a" + tokenFinder));

        int expFinder = PlayerDataManager.getHoeEnchant(player, "expfinder");
        if (expFinder > 0) enchants.add(MessageUtils.color("&#ABDAFA▎ &fExp Finder: &a" + expFinder));

        int moneyFinder = PlayerDataManager.getHoeEnchant(player, "moneyfinder");
        if (moneyFinder > 0) enchants.add(MessageUtils.color("&#ABDAFA▎ &fMoney Finder: &a" + moneyFinder));

        int shardInstinct = PlayerDataManager.getHoeEnchant(player, "shardinstinct");
        if (shardInstinct > 0) enchants.add(MessageUtils.color("&#ABDAFA▎ &fShard Instinct: &a" + shardInstinct));

        int burst = PlayerDataManager.getHoeEnchant(player, "burst");
        if (burst > 0) enchants.add(MessageUtils.color("&#ABDAFA▎ &fBurst: &a" + burst));

        int flyingTokens = PlayerDataManager.getHoeEnchant(player, "flyingtokens");
        if (flyingTokens > 0) enchants.add(MessageUtils.color("&#ABDAFA▎ &fFlying Tokens: &a" + flyingTokens));

        int flyingExp = PlayerDataManager.getHoeEnchant(player, "flyingexp");
        if (flyingExp > 0) enchants.add(MessageUtils.color("&#ABDAFA▎ &fFlying EXP: &a" + flyingExp));

        return enchants;
    }
}

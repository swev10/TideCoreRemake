package com.tideCore.util;

import com.tideCore.TideCore;
import org.bukkit.NamespacedKey;

public class TideCoreKeys {

    public static final NamespacedKey HARVESTER_KEY = new NamespacedKey(TideCore.getInstance(), "harvester_hoe");
    public static final NamespacedKey BOOSTER_KEY = new NamespacedKey(TideCore.getInstance(), "booster_data");

    public static NamespacedKey getUpgradeKey(String enchantName) {
        return new NamespacedKey(TideCore.getInstance(), "upgrade_" + enchantName.toLowerCase().replace(" ", "_"));
    }
}

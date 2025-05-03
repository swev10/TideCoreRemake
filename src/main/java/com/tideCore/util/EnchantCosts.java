package com.tideCore.util;

import java.util.HashMap;
import java.util.Map;

public class EnchantCosts {

    private static final Map<String, EnchantData> enchantData = new HashMap<>();

    static {
        register("tokenfinder", 250, 0.10);
        register("expfinder", 250, 0.10);
        register("moneyfinder", 250, 0.10);
        register("flyingtokens", 250, 0.10);
        register("flyingexp", 250, 0.10);
        register("shardinstinct", 500, 0.15);
        register("burst", 500, 0.15);
    }

    private static void register(String enchant, int baseCost, double multiplier) {
        enchantData.put(enchant, new EnchantData(baseCost, multiplier));
    }

    public static int getCost(String enchant, int currentLevel) {
        if (!enchantData.containsKey(enchant)) return 0;
        EnchantData data = enchantData.get(enchant);
        return (int) (data.baseCost + (data.baseCost * (currentLevel * data.multiplier)));
    }

    private static class EnchantData {
        final int baseCost;
        final double multiplier;

        EnchantData(int baseCost, double multiplier) {
            this.baseCost = baseCost;
            this.multiplier = multiplier;
        }
    }
}

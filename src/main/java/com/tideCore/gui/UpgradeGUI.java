package com.tideCore.gui;

import com.tideCore.managers.PlayerDataManager;
import com.tideCore.util.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class UpgradeGUI implements Listener {

    private static final Map<UUID, String> selectedEnchant = new HashMap<>();
    public static final String GUI_TITLE = MessageUtils.color("&#ABDAFA&lHarvester Upgrades");

    public static void open(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, GUI_TITLE);

        ItemStack filler = createGlass();
        for (int i = 0; i < gui.getSize(); i++) {
            gui.setItem(i, filler);
        }

        gui.setItem(10, createEnchantItem(player, "tokenfinder", "Token Finder", Material.SUNFLOWER, 3));
        gui.setItem(11, createEnchantItem(player, "expfinder", "EXP Finder", Material.EXPERIENCE_BOTTLE, 3));
        gui.setItem(12, createEnchantItem(player, "moneyfinder", "Money Finder", Material.GOLD_NUGGET, 3));
        gui.setItem(14, createEnchantItem(player, "shardinstinct", "Shard Instinct", Material.PRISMARINE_SHARD, 5));
        gui.setItem(15, createEnchantItem(player, "burst", "Burst", Material.FIRE_CHARGE, 5));
        gui.setItem(16, createEnchantItem(player, "flyingtokens", "Flying Tokens", Material.ENDER_PEARL, 10));
        gui.setItem(22, createEnchantItem(player, "flyingexp", "Flying EXP", Material.NETHER_STAR, 10));

        player.openInventory(gui);
    }

    private static ItemStack createEnchantItem(Player player, String id, String name, Material mat, int prestigeRequired) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(MessageUtils.color("&#ABDAFA" + name));

        List<String> lore = new ArrayList<>();
        int currentLevel = PlayerDataManager.getHoeEnchant(player, id);

        lore.add(MessageUtils.color("&7Current Level: &f" + currentLevel));
        lore.add("");

        if (PlayerDataManager.getPrestige(player) >= prestigeRequired) {
            lore.add(MessageUtils.color("&aClick to upgrade!"));
        } else {
            lore.add(MessageUtils.color("&cRequires Prestige " + prestigeRequired));
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack createGlass() {
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(" ");
        glass.setItemMeta(meta);
        return glass;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getView().getTitle().equals(GUI_TITLE)) return;

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta() || !clicked.getItemMeta().hasDisplayName()) return;

        String name = MessageUtils.stripColor(clicked.getItemMeta().getDisplayName()).toLowerCase();

        String id = switch (name) {
            case "token finder" -> "tokenfinder";
            case "exp finder" -> "expfinder";
            case "money finder" -> "moneyfinder";
            case "shard instinct" -> "shardinstinct";
            case "burst" -> "burst";
            case "flying tokens" -> "flyingtokens";
            case "flying exp" -> "flyingexp";
            default -> null;
        };

        if (id == null) return;

        int prestigeRequired = switch (id) {
            case "tokenfinder", "expfinder", "moneyfinder" -> 3;
            case "shardinstinct", "burst" -> 5;
            case "flyingtokens", "flyingexp" -> 10;
            default -> 0;
        };

        if (PlayerDataManager.getPrestige(player) < prestigeRequired) {
            player.sendMessage(MessageUtils.prefix() + "§cYou need Prestige " + prestigeRequired + " to upgrade this enchant.");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f);
            return;
        }

        setSelectedEnchant(player.getUniqueId(), id);
        UpgradeSelectorGUI.open(player, id);
    }

    public static void setSelectedEnchant(UUID uuid, String enchant) {
        selectedEnchant.put(uuid, enchant);
    }

    public static String getSelectedEnchant(UUID uuid) {
        return selectedEnchant.get(uuid);
    }
}

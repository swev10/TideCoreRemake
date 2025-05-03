package com.tideCore.gui;

import com.tideCore.TideCore;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.List;

public class PrestigeGUI implements Listener {

    private static final String GUI_TITLE = "§b§lPrestige Menu";

    public static void open(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, GUI_TITLE);
        fillBorder(gui, Material.LIGHT_BLUE_STAINED_GLASS_PANE);

        ItemStack prestigeButton = createPrestigeButton(player);
        gui.setItem(13, prestigeButton);

        player.openInventory(gui);
        animateBorder(gui, player);
    }

    private static ItemStack createPrestigeButton(Player player) {
        int currentPrestige = PlayerDataManager.getPrestige(player);
        int nextPrestige = currentPrestige + 1;
        int currentLevel = PlayerDataManager.getLevel(player);
        double requiredMoney = 10000 * Math.pow(1.5, currentPrestige);
        int requiredLevel = 10 + (currentPrestige * 5);

        boolean hasLevel = currentLevel >= requiredLevel;
        boolean hasMoney = TideCore.getEconomy().getBalance(player) >= requiredMoney;
        boolean canPrestige = hasLevel && hasMoney;

        ItemStack egg = new ItemStack(Material.DRAGON_EGG);
        ItemMeta meta = egg.getItemMeta();

        meta.setDisplayName("§8[§x§C§C§A§B§F§D🔥§8] §x§0§0§B§7§F§F§lPRESTIGE" + (canPrestige ? " §8(§a✓§8)" : " §8(§c✘§8)"));
        DecimalFormat df = new DecimalFormat("#,###.##");
        meta.setLore(List.of(
                "§8ᴘʀᴇѕᴛɪɢᴇ ʙᴜᴛᴛᴏɴ",
                "",
                "§x§0§0§B§7§F§F▎ ɪɴꜰᴏʀᴍᴀᴛɪᴏɴ",
                "§x§0§0§B§7§F§F▎ §fʀᴇᴍᴏᴠᴇs ʟᴇᴠᴇʟ + ᴇxᴘ",
                "",
                "§x§0§0§B§7§F§F▎ ʀᴇǫᴜɪʀᴇᴍᴇɴᴛs",
                "§x§0§0§B§7§F§F▎ §fᴍᴏɴᴇʏ: §e$" + df.format(requiredMoney),
                "§x§0§0§B§7§F§F▎ §fʟᴇᴠᴇʟ: §b" + requiredLevel,
                "§x§0§0§B§7§F§F▎ §fᴇʟɪɢɪʙʟᴇ: " + (canPrestige ? "§a✓" : "§c✘"),
                "",
                canPrestige ? "§aᴄʟɪᴄᴋ ᴛᴏ ᴘʀᴇsᴛɪɢᴇ!" : "§cʏᴏᴜ ᴅᴏ ɴᴏᴛ ᴍᴇᴇᴛ ᴛʜᴇ ʀᴇǫᴜɪʀᴇᴍᴇɴᴛs!"
        ));
        egg.setItemMeta(meta);
        return egg;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (!e.getView().getTitle().equals(GUI_TITLE)) return;
        e.setCancelled(true);

        if (e.getSlot() != 13) return;

        int prestige = PlayerDataManager.getPrestige(player);
        int level = PlayerDataManager.getLevel(player);
        double balance = TideCore.getEconomy().getBalance(player);
        double cost = 10000 * Math.pow(1.5, prestige);
        int requiredLevel = 10 + (prestige * 5);

        if (level < requiredLevel) {
            player.sendMessage(MessageUtils.prefix() + "§cYou need level §e" + requiredLevel + " §cto prestige.");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f);
            player.closeInventory();
            return;
        }

        if (balance < cost) {
            player.sendMessage(MessageUtils.prefix() + "§cYou need §e$" + new DecimalFormat("#,###.##").format(cost) + " §cto prestige.");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f);
            player.closeInventory();
            return;
        }

        TideCore.getEconomy().withdrawPlayer(player, cost);
        PlayerDataManager.setLevel(player, 1);
        PlayerDataManager.setXP(player, 0);
        PlayerDataManager.setPrestige(player, prestige + 1);

        player.sendTitle("§b§lPRESTIGED!", "§fYou're now §ePrestige " + (prestige + 1), 10, 60, 10);
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1.2f);

        int newPrestige = prestige + 1;
        String color = MessageUtils.prestigeColor(newPrestige);
        String message = MessageUtils.prefix() + "§b" + player.getName() + " §ahas prestiged to " + color + "Prestige " + newPrestige + "§a!";
        player.sendMessage(message);

        if (TideCore.getInstance().getConfig().getBoolean("prestige.broadcast", true)) {
            Bukkit.broadcastMessage(message);
        }

        player.closeInventory();
    }

    private static void fillBorder(Inventory gui, Material border) {
        ItemStack pane = new ItemStack(border);
        ItemMeta meta = pane.getItemMeta();
        meta.setDisplayName(" ");
        pane.setItemMeta(meta);

        for (int i = 0; i < gui.getSize(); i++) {
            if (i < 9 || i > 17 || i % 9 == 0 || i % 9 == 8) {
                gui.setItem(i, pane);
            }
        }
    }

    private static void animateBorder(Inventory gui, Player player) {
        new BukkitRunnable() {
            final Material[] cycle = {Material.LIGHT_BLUE_STAINED_GLASS_PANE, Material.BLUE_STAINED_GLASS_PANE};
            int tick = 0;

            @Override
            public void run() {
                if (!player.isOnline() || !player.getOpenInventory().getTitle().equals(GUI_TITLE)) {
                    cancel();
                    return;
                }
                fillBorder(gui, cycle[tick % cycle.length]);
                tick++;
            }
        }.runTaskTimer(TideCore.getInstance(), 0L, 20L);
    }
}

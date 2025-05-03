package com.tideCore.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.tideCore.TideCore;
import com.tideCore.gui.UpgradeGUI;
import com.tideCore.managers.BoosterManager;
import com.tideCore.managers.PlayerDataManager;
import com.tideCore.util.MessageUtils;
import com.tideCore.util.TideCoreKeys;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class HarvesterListener implements Listener {

    private final Set<Material> allowedCrops = Set.of(
            Material.WHEAT, Material.CARROTS, Material.POTATOES, Material.BEETROOTS, Material.NETHER_WART
    );

    private final Random random = new Random();

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material type = block.getType();

        if (!isInTidecoreRegion(player)) return;
        if (!isReplaceableCrop(type)) return;
        if (!(block.getBlockData() instanceof Ageable ageable)) return;

        if (!isHarvesterHoe(player.getInventory().getItemInMainHand())) {
            event.setCancelled(true);
            player.sendMessage(MessageUtils.prefix() + "§cYou must use your Harvester Hoe to harvest crops.");
            return;
        }

        if (ageable.getAge() < ageable.getMaximumAge()) {
            event.setCancelled(true);
            player.sendMessage(MessageUtils.prefix() + "§cThis crop is not fully grown yet.");
            return;
        }

        event.setCancelled(false);
        Material originalCrop = block.getType();
        block.setType(Material.AIR);

        rewardPlayer(player);
        tryEnchantEffects(player, block);

        animateRegrow(block.getLocation(), originalCrop);
    }

    private void rewardPlayer(Player player) {
        int baseXP = 5;
        int baseTokens = 1;
        double baseMoney = 2.50;

        // Apply enchant multipliers
        int tokenLevel = PlayerDataManager.getHoeEnchant(player, "tokenfinder");
        int expLevel = PlayerDataManager.getHoeEnchant(player, "expfinder");
        int moneyLevel = PlayerDataManager.getHoeEnchant(player, "moneyfinder");

        int tokens = (int) Math.round(baseTokens + (baseTokens * 0.15 * tokenLevel));
        int rawXP = (int) Math.round(baseXP + (baseXP * 0.10 * expLevel));
        double money = baseMoney + (baseMoney * 0.05 * moneyLevel);

        int boostedXP = BoosterManager.getBoostedXP(rawXP, player);
        int levelBefore = PlayerDataManager.getLevel(player);

        // Apply rewards
        PlayerDataManager.addXP(player, boostedXP);
        PlayerDataManager.addTokens(player, tokens);
        TideCore.getEconomy().depositPlayer(player, money);

        // Level up title
        int levelAfter = PlayerDataManager.getLevel(player);
        if (levelAfter > levelBefore) {
            player.sendTitle("§a⬆ LEVEL UP!", "§7Level " + levelAfter, 10, 40, 10);
        }

        // Action Bar
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                new TextComponent("§8+§a$" + String.format("%.2f", money)
                        + " §8| §8+§b🧪 " + boostedXP + " XP §8| §8+§e★" + tokens + " Tokens"));
    }

    private void tryEnchantEffects(Player player, Block center) {
        if (hasUnlocked(player, "shardinstinct") && chance(5)) {
            player.getInventory().addItem(createShardItem());
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
        }

        if (hasUnlocked(player, "burst") && chance(5)) {
            burstNearbyCrops(player, center);
            player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1f, 1.2f);
        }

        if (hasUnlocked(player, "flyingtokens") && chance(2)) {
            int bonusTokens = 100 + random.nextInt(200);
            PlayerDataManager.addTokens(player, bonusTokens);
            player.sendMessage(MessageUtils.prefix() + "§eFlying Tokens! §f+§e" + bonusTokens + " Tokens!");
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1.2f);
        }

        if (hasUnlocked(player, "flyingexp") && chance(2)) {
            int bonusXP = 100 + random.nextInt(150);
            PlayerDataManager.addXP(player, bonusXP);
            player.sendMessage(MessageUtils.prefix() + "§bFlying EXP! §f+§b" + bonusXP + " XP!");
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1.2f);
        }
    }

    private ItemStack createShardItem() {
        ItemStack shard = new ItemStack(Material.PRISMARINE_SHARD);
        ItemMeta meta = shard.getItemMeta();
        meta.setDisplayName(MessageUtils.color("&#ABDAFA⚡ Armor Fragment"));
        meta.setLore(List.of(MessageUtils.color("&7A rare shard dropped while farming.")));
        shard.setItemMeta(meta);
        return shard;
    }

    private void burstNearbyCrops(Player player, Block center) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Block nearby = center.getRelative(x, 0, z);
                if (allowedCrops.contains(nearby.getType()) && (nearby.getBlockData() instanceof Ageable ageable) && ageable.getAge() == ageable.getMaximumAge()) {
                    nearby.setType(Material.AIR);
                    rewardPlayer(player);
                    animateRegrow(nearby.getLocation(), nearby.getType());
                }
            }
        }
    }

    private void animateRegrow(Location location, Material crop) {
        new BukkitRunnable() {
            int stage = 0;
            final int maxStage = ((Ageable) Bukkit.createBlockData(crop)).getMaximumAge();

            @Override
            public void run() {
                if (stage == 0) {
                    location.getBlock().setType(crop);
                }
                if (location.getBlock().getType() != crop) {
                    cancel();
                    return;
                }
                Ageable ageable = (Ageable) location.getBlock().getBlockData();
                ageable.setAge(Math.min(stage, ageable.getMaximumAge()));
                location.getBlock().setBlockData(ageable);
                stage++;
                if (stage > maxStage) cancel();
            }
        }.runTaskTimer(TideCore.getInstance(), 0L, 5L);
    }

    private boolean hasUnlocked(Player player, String enchant) {
        return PlayerDataManager.getHoeEnchant(player, enchant) > 0;
    }

    private boolean chance(int percent) {
        return random.nextInt(100) < percent;
    }

    private boolean isInTidecoreRegion(Player player) {
        RegionManager rm = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(player.getWorld()));
        if (rm == null) return false;

        ApplicableRegionSet regions = rm.getApplicableRegions(BukkitAdapter.asBlockVector(player.getLocation()));
        for (ProtectedRegion r : regions) {
            if (r.getId().equalsIgnoreCase("tidecore_spawn")) return true;
        }
        return false;
    }

    private boolean isReplaceableCrop(Material mat) {
        return allowedCrops.contains(mat);
    }

    private boolean isHarvesterHoe(ItemStack item) {
        if (item == null || item.getType() != Material.WOODEN_HOE) return false;
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(TideCoreKeys.HARVESTER_KEY, PersistentDataType.INTEGER);
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (!event.getAction().toString().contains("RIGHT_CLICK")) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() != Material.WOODEN_HOE) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.getPersistentDataContainer().has(TideCoreKeys.HARVESTER_KEY, PersistentDataType.INTEGER)) return;

        event.setCancelled(true);
        UpgradeGUI.open(player);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
    }

    @EventHandler
    public void onTrample(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL &&
                event.getClickedBlock() != null &&
                event.getClickedBlock().getType() == Material.FARMLAND) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityTrample(EntityInteractEvent event) {
        if (event.getBlock().getType() == Material.FARMLAND) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFade(BlockFadeEvent event) {
        if (event.getBlock().getType() == Material.FARMLAND) {
            event.setCancelled(true);
        }
    }
}

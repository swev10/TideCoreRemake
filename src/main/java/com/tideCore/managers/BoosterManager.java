package com.tideCore.managers;

import com.tideCore.TideCore;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BoosterManager {

    private static final Map<UUID, BoosterData> boosters = new HashMap<>();
    private static final Map<UUID, BossBar> bossBars = new HashMap<>();

    public static void applyBooster(Player player, int minutes, double multiplier) {
        UUID uuid = player.getUniqueId();

        boosters.put(uuid, new BoosterData(multiplier, minutes * 60));
        createBossBar(player, multiplier, minutes * 60);

        new BukkitRunnable() {
            int secondsLeft = minutes * 60;

            @Override
            public void run() {
                if (!boosters.containsKey(uuid)) {
                    cancel();
                    return;
                }

                BoosterData data = boosters.get(uuid);
                data.timeLeft--;

                if (data.timeLeft <= 0) {
                    boosters.remove(uuid);
                    removeBossBar(player);
                    cancel();
                    return;
                }

                updateBossBar(player, data);
            }
        }.runTaskTimer(TideCore.getInstance(), 20L, 20L);
    }

    public static boolean hasBooster(Player player) {
        return boosters.containsKey(player.getUniqueId());
    }

    public static double getMultiplier(Player player) {
        return boosters.containsKey(player.getUniqueId()) ?
                boosters.get(player.getUniqueId()).multiplier : 1.0;
    }

    public static int getBoostedXP(int baseXP, Player player) {
        double multiplier = getMultiplier(player);
        return (int) Math.round(baseXP * multiplier);
    }

    private static void createBossBar(Player player, double multiplier, int seconds) {
        String title = "§aHarvest Booster §7» §f" + multiplier + "x §8(" + formatTime(seconds) + ")";
        BossBar bar = Bukkit.createBossBar(title, BarColor.GREEN, BarStyle.SEGMENTED_10);
        bar.setProgress(1.0);
        bar.addPlayer(player);
        bossBars.put(player.getUniqueId(), bar);
    }

    private static void updateBossBar(Player player, BoosterData data) {
        BossBar bar = bossBars.get(player.getUniqueId());
        if (bar == null) return;

        double progress = Math.max(0, (double) data.timeLeft / (data.originalTime));
        bar.setProgress(progress);

        bar.setTitle("§aHarvest Booster §7» §f" + data.multiplier + "x §8(" + formatTime(data.timeLeft) + ")");
    }

    private static void removeBossBar(Player player) {
        BossBar bar = bossBars.remove(player.getUniqueId());
        if (bar != null) {
            bar.removeAll();
        }
    }

    private static String formatTime(int seconds) {
        int mins = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", mins, secs);
    }

    private static class BoosterData {
        double multiplier;
        int timeLeft;
        final int originalTime;

        BoosterData(double multiplier, int timeLeft) {
            this.multiplier = multiplier;
            this.timeLeft = timeLeft;
            this.originalTime = timeLeft;
        }
    }
}

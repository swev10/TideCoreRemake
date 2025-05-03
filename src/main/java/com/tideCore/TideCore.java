package com.tideCore;

import com.tideCore.commands.PrestigeCommand;
import com.tideCore.commands.TideCoreCommand;
import com.tideCore.commands.UpgradeStationReloadCommand;
import com.tideCore.gui.PrestigeGUI;
import com.tideCore.gui.UpgradeGUI;
import com.tideCore.gui.UpgradeSelectorGUI;
import com.tideCore.holograms.UpgradeStationManager;
import com.tideCore.listeners.BoosterRedeemListener;
import com.tideCore.listeners.HarvesterListener;
import com.tideCore.placeholders.TidegenPlaceholders;
import com.tideCore.util.MessageUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class TideCore extends JavaPlugin {

    private static TideCore instance;
    private static Economy econ;

    @Override
    public void onLoad() {
        File playerDataFolder = new File(getDataFolder(), "players");
        if (!playerDataFolder.exists()) playerDataFolder.mkdirs();
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        MessageUtils.reloadPrefix();
        UpgradeStationManager.loadStations();
        Bukkit.getPluginManager().registerEvents(new UpgradeStationManager(), this);

        Bukkit.getPluginManager().registerEvents(new PrestigeGUI(), this);
        Bukkit.getPluginManager().registerEvents(new UpgradeSelectorGUI(), this);
        Bukkit.getPluginManager().registerEvents(new HarvesterListener(), this);
        Bukkit.getPluginManager().registerEvents(new BoosterRedeemListener(), this);
        Bukkit.getPluginManager().registerEvents(new UpgradeGUI(), this);

        UpgradeStationManager.loadStations();

        if (!setupEconomy()) {
            getLogger().severe("Vault not found or economy setup failed. Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getCommand("tidecore").setExecutor(new TideCoreCommand());
        getCommand("prestige").setExecutor(new PrestigeCommand());
        getCommand("upgradestations").setExecutor(new UpgradeStationReloadCommand());

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new TidegenPlaceholders().register();
            getLogger().info("PlaceholderAPI hooked: %tidegen_*% registered");
        }

        printSplash("ENABLED");
    }

    @Override
    public void onDisable() {
        UpgradeStationManager.clearAll();
        printSplash("DISABLED");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        econ = rsp.getProvider();
        return econ != null;
    }

    public static TideCore getInstance() {
        return instance;
    }

    public static Economy getEconomy() {
        return econ;
    }

    private void printSplash(String state) {
        getLogger().info("§b----=[ TIDECORE " + state + " ]=----");
        getLogger().info("§7Developed By SwevMC - https://swevmc.cloud");
    }
}

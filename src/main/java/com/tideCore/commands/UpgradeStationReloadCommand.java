package com.tideCore.commands;

import com.tideCore.holograms.UpgradeStationManager;
import com.tideCore.util.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class UpgradeStationReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("tidecore.admin")) {
            sender.sendMessage(MessageUtils.prefix() + "§cYou do not have permission to reload upgrade stations.");
            return true;
        }

        UpgradeStationManager.clearAll();
        UpgradeStationManager.loadStations();

        sender.sendMessage(MessageUtils.prefix() + "§aUpgrade stations reloaded!");
        return true;
    }
}

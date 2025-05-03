package com.tideCore.commands;

import com.tideCore.items.BoosterFactory;
import com.tideCore.items.HarvesterFactory;
import com.tideCore.util.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class GiveCommand implements SubCommand {

    @Override
    public String getName() {
        return "give";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(MessageUtils.prefix() + "&cUsage: /tidecore give <harvesterhoe|booster> ...");
            return;
        }

        if (args[1].equalsIgnoreCase("harvesterhoe")) {
            if (args.length != 3) {
                sender.sendMessage(MessageUtils.prefix() + "&cUsage: /tidecore give harvesterhoe <player>");
                return;
            }

            Player target = Bukkit.getPlayerExact(args[2]);
            if (target == null) {
                sender.sendMessage(MessageUtils.prefix() + "&cPlayer not found.");
                return;
            }

            ItemStack hoe = HarvesterFactory.createHarvesterHoe(target);
            target.getInventory().addItem(hoe);
            sender.sendMessage(MessageUtils.prefix() + "&aGave a harvester hoe to &e" + target.getName());
            return;
        }

        if (args[1].equalsIgnoreCase("booster")) {
            if (args.length != 5) {
                sender.sendMessage(MessageUtils.prefix() + "&cUsage: /tidecore give booster <minutes> <multiplier> <player>");
                return;
            }

            try {
                int minutes = Integer.parseInt(args[2]);
                double multiplier = Double.parseDouble(args[3]);
                Player target = Bukkit.getPlayerExact(args[4]);
                if (target == null) {
                    sender.sendMessage(MessageUtils.prefix() + "&cPlayer not found.");
                    return;
                }
                ItemStack booster = BoosterFactory.createBooster(multiplier, minutes);
                target.getInventory().addItem(booster);
                sender.sendMessage(MessageUtils.prefix() + "&aGave a &e" + multiplier + "x &abooster for &e" + minutes + "m to &e" + target.getName());
            } catch (NumberFormatException e) {
                sender.sendMessage(MessageUtils.prefix() + "&cInvalid number input.");
            }
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) return List.of("harvesterhoe", "booster");
        if (args.length == 3 && args[1].equalsIgnoreCase("harvesterhoe")) {
            return null;
        }
        if (args.length == 5 && args[1].equalsIgnoreCase("booster")) {
            return null;
        }
        return Collections.emptyList();
    }
}

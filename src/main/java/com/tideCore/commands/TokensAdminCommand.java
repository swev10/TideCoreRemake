package com.tideCore.commands;

import com.tideCore.commands.SubCommand;
import com.tideCore.managers.PlayerDataManager;
import com.tideCore.util.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class TokensAdminCommand implements SubCommand {

    @Override
    public String getName() {
        return "tokens";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage(MessageUtils.prefix() + "§cUsage: /tidecore tokens <give|set|take> <player> <amount>");
            return;
        }

        String action = args[1].toLowerCase();
        Player target = Bukkit.getPlayerExact(args[2]);
        if (target == null) {
            sender.sendMessage(MessageUtils.prefix() + "§cPlayer not found: " + args[2]);
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage(MessageUtils.prefix() + "§cAmount must be a number.");
            return;
        }

        switch (action) {
            case "give" -> {
                PlayerDataManager.addTokens(target, amount);
                sender.sendMessage(MessageUtils.prefix() + "§aGave §e" + amount + " tokens §ato §b" + target.getName());
            }
            case "set" -> {
                PlayerDataManager.setTokens(target, amount);
                sender.sendMessage(MessageUtils.prefix() + "§aSet §b" + target.getName() + "§a's tokens to §e" + amount);
            }
            case "take" -> {
                PlayerDataManager.takeTokens(target, amount);
                sender.sendMessage(MessageUtils.prefix() + "§aTook §e" + amount + " tokens §afrom §b" + target.getName());
            }
            default -> {
                sender.sendMessage(MessageUtils.prefix() + "§cUsage: /tidecore tokens <give|set|take> <player> <amount>");
            }
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) return List.of("give", "set", "take");
        if (args.length == 3) return null;
        return Collections.emptyList();
    }
}

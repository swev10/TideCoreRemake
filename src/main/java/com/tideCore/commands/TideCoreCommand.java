package com.tideCore.commands;

import com.tideCore.util.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TideCoreCommand implements CommandExecutor, TabCompleter {

    private final Map<String, SubCommand> subcommands = new HashMap<>();

    public TideCoreCommand() {
        register(new HelpCommand());
        register(new ReloadCommand());
        register(new TokensAdminCommand());
        register(new PearlsAdminCommand());
        register(new LevelsAdminCommand());
        register(new GiveCommand());
    }

    private void register(SubCommand subCommand) {
        subcommands.put(subCommand.getName().toLowerCase(), subCommand);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("tidecore.admin")) {
            sender.sendMessage(MessageUtils.prefix() + "§cYou don't have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(MessageUtils.prefix() + "§eUse /tidecore help");
            return true;
        }

        SubCommand sub = subcommands.get(args[0].toLowerCase());
        if (sub != null) {
            sub.execute(sender, args);
        } else {
            sender.sendMessage(MessageUtils.prefix() + "§cUnknown subcommand. Use /tidecore help");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("tidecore.admin")) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            return subcommands.keySet().stream()
                    .filter(cmd -> cmd.startsWith(args[0].toLowerCase()))
                    .toList();
        }

        SubCommand sub = subcommands.get(args[0].toLowerCase());
        if (sub != null) {
            return sub.tabComplete(sender, args);
        }
        return Collections.emptyList();
    }
}

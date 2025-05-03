package com.tideCore.commands;

import com.tideCore.util.MessageUtils;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class HelpCommand implements SubCommand {
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(MessageUtils.color("&7&m------------------------------"));
        sender.sendMessage(MessageUtils.prefix() + "&f/tidecore give <...>");
        sender.sendMessage(MessageUtils.prefix() + "&f/tidecore prestige");
        sender.sendMessage(MessageUtils.prefix() + "&f/tidecore selectcrop");
        sender.sendMessage(MessageUtils.prefix() + "&f/tidecore reload");
        sender.sendMessage(MessageUtils.color("&7&m------------------------------"));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}

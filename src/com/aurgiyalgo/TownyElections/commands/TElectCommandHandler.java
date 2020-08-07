package com.aurgiyalgo.TownyElections.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.aurgiyalgo.TownyElections.TownyElections;

public class TElectCommandHandler implements CommandExecutor {

	private TownyElections instance;

	public TElectCommandHandler(TownyElections instance) {
		this.instance = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args) {
		if (args.length < 1) {
			return executeHelp(sender, cmd, str, args);
		}
		switch (args[0]) {
		case "info":
			return executeInfo(sender, cmd, str, args);
		default:
			sender.sendMessage(ChatColor.RED + "Invalid argument!");
			break;
		}
		return true;
	}

	private boolean executeHelp(CommandSender sender, Command cmd, String str, String[] args) {
		for (String line : instance.getHelpMessage()) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
		}
		return true;
	}

	private boolean executeInfo(CommandSender sender, Command cmd, String str, String[] args) {
		for (String line : instance.getInfoMessage()) {
			line = line.replaceAll("%description%", instance.getDescription().getDescription());
			line = line.replaceAll("%version%", instance.getDescription().getVersion());
			line = line.replaceAll("%author%", instance.getDescription().getAuthors().get(0));
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
		}
		return true;
	}

}

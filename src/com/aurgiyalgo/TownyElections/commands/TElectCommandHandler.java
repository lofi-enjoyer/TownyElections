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
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', TownyElections.Text.ELECTIONS_HELP_MESSAGE));
		return true;
	}

	private boolean executeInfo(CommandSender sender, Command cmd, String str, String[] args) {
		String infoMessage = TownyElections.Text.INFO_MESSAGE;
		infoMessage = infoMessage.replaceAll("%description%", instance.getDescription().getDescription());
		infoMessage = infoMessage.replaceAll("%version%", instance.getDescription().getVersion());
		infoMessage = infoMessage.replaceAll("%author%", instance.getDescription().getAuthors().get(0));
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', infoMessage));
		return true;
	}

}

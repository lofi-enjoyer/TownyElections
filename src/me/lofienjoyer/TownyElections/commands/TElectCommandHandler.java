package me.lofienjoyer.TownyElections.commands;

import me.lofienjoyer.TownyElections.TownyElections;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

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
		case "reload":
			return executeReload(sender, cmd, str, args);
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
		String infoMessage = TownyElections.Text.INFO_MESSAGE
				.replaceAll("%description%", instance.getDescription().getDescription())
				.replaceAll("%version%", instance.getDescription().getVersion())
				.replaceAll("%author%", instance.getDescription().getAuthors().get(0));
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', infoMessage));
		return true;
	}

	private boolean executeReload(CommandSender sender, Command cmd, String str, String[] args) {
		if (!TownyElections.hasPerms((Player) sender, "townyelections.reload")) return true;
		instance.getLanguageData().load();
		instance.getLanguageData().getString("plugin-reloaded");
		return true;
	}

}

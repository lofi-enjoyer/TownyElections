package me.lofienjoyer.TownyElections.commands;

import me.lofienjoyer.TownyElections.TownyElections;
import me.lofienjoyer.TownyElections.commands.elections.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ElectionsCommandHandler extends CommandHandler {

	public ElectionsCommandHandler() {
		addSubCommand(new ElectionsVoteSubCommand());
		addSubCommand(new ElectionsConvokeSubCommand());
		addSubCommand(new ElectionsListSubCommand());
		addSubCommand(new ElectionsStopSubCommand());
		addSubCommand(new ElectionsUnvoteSubCommand());
	}

	@Override
	protected boolean executeHelp(CommandSender sender) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', TownyElections.Text.ELECTIONS_HELP_MESSAGE));
		return true;
	}

}

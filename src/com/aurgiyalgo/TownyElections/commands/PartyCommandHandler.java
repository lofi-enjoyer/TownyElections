package com.aurgiyalgo.TownyElections.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.aurgiyalgo.TownyElections.TownyElections;
import com.aurgiyalgo.TownyElections.parties.NationParty;
import com.aurgiyalgo.TownyElections.parties.TownParty;

public class PartyCommandHandler implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args) {
		if (args.length < 1) {
			return executeHelp(sender, cmd, str, args);
		}
		switch (args[0].toLowerCase()) {
		case "create":
			return executeCreate(sender, cmd, str, args);
		case "leave":
			return executeLeave(sender, cmd, str, args);
		case "invite":
			return false;
		case "accept":
			return false;
		case "list":
			return false;
		//TODO Invites list
		default:
			return executeHelp(sender, cmd, str, args);
		}
	}

	private boolean executeHelp(CommandSender sender, Command cmd, String str, String[] args) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', TownyElections.getInstance().getHelpMessage()));
		return true;
	}

	private boolean executeCreate(CommandSender sender, Command cmd, String str, String[] args) {
		if (args.length < 3) {
			return executeHelp(sender, cmd, str, args);
		}
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command can only be executed by a player");
			return true;
		}
		Player player = (Player) sender;
		switch (args[1].toLowerCase()) {
		case "town": {
			TownParty party = TownyElections.getInstance().getPartyManager().getPlayerTownParty(player.getUniqueId());
			if (party == null) {
				player.sendMessage(ChatColor.RED + "You are already part of a party");
				return true;
			}
			party = new TownParty(args[2], player.getUniqueId());
			TownyElections.getInstance().getPartyManager().addParty(party);
		}
		return true;
		case "nation": {
			NationParty party = TownyElections.getInstance().getPartyManager().getPlayerNationParty(player.getUniqueId());
			if (party == null) {
				player.sendMessage(ChatColor.RED + "You are already part of a party");
				return true;
			}
			party = new NationParty(args[2], player.getUniqueId());
			TownyElections.getInstance().getPartyManager().addParty(party);
		}
		return true;
		default:
			return executeHelp(sender, cmd, str, args);
		}
	}

	private boolean executeLeave(CommandSender sender, Command cmd, String str, String[] args) {
		if (args.length < 3) {
			return executeHelp(sender, cmd, str, args);
		}
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command can only be executed by a player");
			return true;
		}
		Player player = (Player) sender;
		switch (args[1].toLowerCase()) {
		case "town": {
			TownParty party = TownyElections.getInstance().getPartyManager().getPlayerTownParty(player.getUniqueId());
			if (party == null) {
				player.sendMessage(ChatColor.RED + "You're not part of a party");
				return true;
			}
			party.removeMember(player.getUniqueId());
		}
		return true;
		case "nation": {
			NationParty party = TownyElections.getInstance().getPartyManager().getPlayerNationParty(player.getUniqueId());
			if (party == null) {
				player.sendMessage(ChatColor.RED + "You're not part of a party");
				return true;
			}
			party.removeMember(player.getUniqueId());
		}
		return true;
		default:
			return executeHelp(sender, cmd, str, args);
		}
	}

}

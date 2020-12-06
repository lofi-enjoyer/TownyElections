package com.aurgiyalgo.TownyElections.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.aurgiyalgo.TownyElections.TownyElections;
import com.aurgiyalgo.TownyElections.parties.NationParty;
import com.aurgiyalgo.TownyElections.parties.Party;
import com.aurgiyalgo.TownyElections.parties.TownParty;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

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
		case "add":
			return executeAdd(sender, cmd, str, args);
		case "accept":
			return executeAccept(sender, cmd, str, args);
		case "invites":
			return executeInvites(sender, cmd, str, args);
		case "setleader":
			return executeSetLeader(sender, cmd, str, args);
		case "promote":
			return executePromote(sender, cmd, str, args);
		case "demote":
			return executeDemote(sender, cmd, str, args);
		case "info":
			return executeInfo(sender, cmd, str, args);
		default:
			return executeHelp(sender, cmd, str, args);
		}
	}

	private boolean executeHelp(CommandSender sender, Command cmd, String str, String[] args) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', TownyElections.Text.HELP_MESSAGE));
		return true;
	}

	private boolean executeCreate(CommandSender sender, Command cmd, String str, String[] args) {
		if (args.length < 3) {
			return notEnoughArguments(sender, cmd, str, args);
		}

		if (!isPlayer(sender)) return true;
		
		Player player = (Player) sender;
		switch (args[1].toLowerCase()) {
		case "town": {
			if (!player.hasPermission(TownyElections.Permissions.TOWN_CONVOKE)) {
				player.sendMessage(ChatColor.RED + "You cannot do that!");
				return true;
			}
			TownParty party = TownyElections.getInstance().getPartyManager().getPlayerTownParty(player.getUniqueId());
			if (party != null) {
				player.sendMessage(ChatColor.RED + "You are already part of a party");
				return true;
			}
			Town town;
			try {
				town = TownyUniverse.getInstance().getDataSource().getResident(player.getName()).getTown();
			} catch (NotRegisteredException e) {
				player.sendMessage(ChatColor.RED + "You are not part of a town!");
				return true;
			}
			if (TownyElections.getInstance().getPartyManager().getPartiesForTown(town.getName()).stream().filter(prty -> prty.getName().toLowerCase().equals(args[2].toLowerCase())).collect(Collectors.toList()).size() > 0) {
				player.sendMessage(ChatColor.RED + "That name is already taken!");
				return true;
			}
			party = new TownParty(args[2], player.getUniqueId(), town.getUuid());
			TownyElections.getInstance().getPartyManager().addParty(party);
			player.sendMessage(ChatColor.GREEN + "The party was succesfully created!");
		}
		return true;
		case "nation": {
			if (!player.hasPermission(TownyElections.Permissions.NATION_CONVOKE)) {
				player.sendMessage(ChatColor.RED + "You cannot do that!");
				return true;
			}
			NationParty party = TownyElections.getInstance().getPartyManager().getPlayerNationParty(player.getUniqueId());
			if (party != null) {
				player.sendMessage(ChatColor.RED + "You are already part of a party");
				return true;
			}
			Nation nation;
			try {
				nation = TownyUniverse.getInstance().getDataSource().getResident(player.getName()).getTown().getNation();
			} catch (NotRegisteredException e) {
				player.sendMessage(ChatColor.RED + "You are not part of a nation!");
				return true;
			}
			if (TownyElections.getInstance().getPartyManager().getPartiesForNation(nation.getName()).stream().filter(prty -> prty.getName().toLowerCase().equals(args[2].toLowerCase())).collect(Collectors.toList()).size() > 0) {
				player.sendMessage(ChatColor.RED + "That name is already taken!");
				return true;
			}
			party = new NationParty(args[2], player.getUniqueId(), nation.getUuid());
			TownyElections.getInstance().getPartyManager().addParty(party);
			player.sendMessage(ChatColor.GREEN + "The party was succesfully created!");
		}
		return true;
		default:
			return executeHelp(sender, cmd, str, args);
		}
	}

	private boolean executeLeave(CommandSender sender, Command cmd, String str, String[] args) {
		if (args.length < 2) {
			return notEnoughArguments(sender, cmd, str, args);
		}
		
		if (!isPlayer(sender)) return true;
		
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

	private boolean executeAdd(CommandSender sender, Command cmd, String str, String[] args) {
		if (args.length < 3) {
			return notEnoughArguments(sender, cmd, str, args);
		}
		
		if (!isPlayer(sender)) return true;
		
		Player player = (Player) sender;
		switch (args[1].toLowerCase()) {
		case "town": {
			TownParty party = TownyElections.getInstance().getPartyManager().getPlayerTownParty(player.getUniqueId());
			if (party == null) {
				player.sendMessage(ChatColor.RED + "You're not part of a party");
				return true;
			}
			if (!party.getLeader().equals(player.getUniqueId()) && !party.isAssistant(player.getUniqueId())) {
				player.sendMessage(ChatColor.RED + "You cannot do that");
				return true;
			}
			Player invitedPlayer = Bukkit.getPlayer(args[2]);
			if (party.isInvited(invitedPlayer.getUniqueId())) {
				player.sendMessage(ChatColor.RED + "That player is already invited");
				return true;
			}
			party.addInvite(invitedPlayer.getUniqueId());
			player.sendMessage(ChatColor.GREEN + invitedPlayer.getName() + " was invited to the party");
			invitedPlayer.sendMessage(ChatColor.GREEN + "You were invited to join the town party " + party.getName());
		}
		return true;
		case "nation": {
			NationParty party = TownyElections.getInstance().getPartyManager().getPlayerNationParty(player.getUniqueId());
			if (party == null) {
				player.sendMessage(ChatColor.RED + "You're not part of a party");
				return true;
			}
			if (!party.getLeader().equals(player.getUniqueId()) && !party.isAssistant(player.getUniqueId())) {
				player.sendMessage(ChatColor.RED + "You cannot do that");
				return true;
			}
			Player invitedPlayer = Bukkit.getPlayer(args[2]);
			if (party.isInvited(invitedPlayer.getUniqueId())) {
				player.sendMessage(ChatColor.RED + "That player is already invited");
				return true;
			}
			party.addInvite(invitedPlayer.getUniqueId());
			player.sendMessage(ChatColor.GREEN + invitedPlayer.getName() + " was invited to the party");
			invitedPlayer.sendMessage(ChatColor.GREEN + "You were invited to join the nation party " + party.getName());
		}
		return true;
		default:
			return executeHelp(sender, cmd, str, args);
		}
	}
	
	private boolean isPlayer(CommandSender sender) {
		if (sender instanceof Player) return true;
		sender.sendMessage(ChatColor.RED + "This command can only be executed by a player");
		return false;
	}

	private boolean executeAccept(CommandSender sender, Command cmd, String str, String[] args) {
		if (args.length < 3) {
			return notEnoughArguments(sender, cmd, str, args);
		}
		
		if (!isPlayer(sender)) return true;
		
		Player player = (Player) sender;
		Resident resident;
		try {
			resident = TownyUniverse.getInstance().getDataSource().getResident(player.getName());
		} catch (NotRegisteredException e) {
			e.printStackTrace();
			return true;
		}
		switch (args[1].toLowerCase()) {
		case "town": {
			TownParty party;
			try {
				party = TownyElections.getInstance().getPartyManager().getPartiesForTown(resident.getTown().getName()).stream().filter(obj -> obj.getName().toLowerCase().equals(args[2])).collect(Collectors.toList()).get(0);
			} catch (NotRegisteredException e) {
				player.sendMessage(ChatColor.RED + "You are not part of a town!");
				return true;
			}
			if (party == null) {
				player.sendMessage(ChatColor.RED + "That party does not exist!");
				return true;
			}
			if (!party.isInvited(player.getUniqueId())) {
				player.sendMessage(ChatColor.RED + "You are not invited to that party");
				return true;
			}
			party.addMember(player.getUniqueId());
			player.sendMessage(ChatColor.GREEN + "You joined the party!");
			for (Player it : Bukkit.getOnlinePlayers()) {
				if (party.getMembers().contains(it.getUniqueId())) it.sendMessage(ChatColor.GREEN + player.getName() + " joined the party!");
			}
		}
		return true;
		case "nation": {
			NationParty party;
			try {
				party = TownyElections.getInstance().getPartyManager().getPartiesForNation(resident.getTown().getNation().getName()).stream().filter(obj -> obj.getName().toLowerCase().equals(args[2])).collect(Collectors.toList()).get(0);
			} catch (NotRegisteredException e) {
				player.sendMessage(ChatColor.RED + "You are not part of a nation!");
				return true;
			}
			if (party == null) {
				player.sendMessage(ChatColor.RED + "That party does not exist!");
				return true;
			}
			if (!party.isInvited(player.getUniqueId())) {
				player.sendMessage(ChatColor.RED + "You are not invited to that party");
				return true;
			}
			party.addMember(player.getUniqueId());
			player.sendMessage(ChatColor.GREEN + "You joined the party!");
			for (Player it : Bukkit.getOnlinePlayers()) {
				if (party.getMembers().contains(it.getUniqueId())) it.sendMessage(ChatColor.GREEN + player.getName() + " joined the party!");
			}
		}
		return true;
		default:
			return executeHelp(sender, cmd, str, args);
		}
	}

	private boolean executeInvites(CommandSender sender, Command cmd, String str, String[] args) {
		if (args.length < 2) {
			return notEnoughArguments(sender, cmd, str, args);
		}
		
		if (!isPlayer(sender)) return true;
		
		Player player = (Player) sender;
		Resident resident;
		try {
			resident = TownyUniverse.getInstance().getDataSource().getResident(player.getName());
		} catch (NotRegisteredException e) {
			e.printStackTrace();
			return true;
		}
		switch (args[1].toLowerCase()) {
		case "town": {
			List<String> parties = new ArrayList<String>();
			try {
				for (Party p : TownyElections.getInstance().getPartyManager().getPartiesForTown(resident.getTown().getName())) 
					if (p.isInvited(player.getUniqueId())) parties.add(p.getName());
			} catch (NotRegisteredException e) {
				player.sendMessage(ChatColor.RED + "You are not part of a town!");
				return true;
			}
			if (parties.size() <= 0) {
				player.sendMessage(ChatColor.RED + "You have no invites!");
				return true;
			}
			StringBuilder builder = new StringBuilder();
			builder.append(ChatColor.GREEN + "List of invites: ");
			builder.append(parties.get(0));
			for (int i = 1; i < parties.size(); i++) {
				builder.append("," + parties.get(i));
			}
		}
		return true;
		case "nation": {
			List<String> parties = new ArrayList<String>();
			try {
				for (Party p : TownyElections.getInstance().getPartyManager().getPartiesForNation(resident.getTown().getNation().getName())) 
					if (p.isInvited(player.getUniqueId())) parties.add(p.getName());
			} catch (NotRegisteredException e) {
				player.sendMessage(ChatColor.RED + "You are not part of a nation!");
				return true;
			}
			if (parties.size() <= 0) {
				player.sendMessage(ChatColor.RED + "You have no invites!");
				return true;
			}
			StringBuilder builder = new StringBuilder();
			builder.append(ChatColor.GREEN + "List of invites: ");
			builder.append(parties.get(0));
			for (int i = 1; i < parties.size(); i++) {
				builder.append("," + parties.get(i));
			}
		}
		return true;
		default:
			return executeHelp(sender, cmd, str, args);
		}
	}

	private boolean executeSetLeader(CommandSender sender, Command cmd, String str, String[] args) {
		if (args.length < 3) {
			return notEnoughArguments(sender, cmd, str, args);
		}

		if (!isPlayer(sender)) return true;
		
		Player player = (Player) sender;
		switch (args[1].toLowerCase()) {
		case "town": {
			TownParty party = TownyElections.getInstance().getPartyManager().getPlayerTownParty(player.getUniqueId());
			if (party == null) {
				player.sendMessage(ChatColor.RED + "You are not part of a party");
				return true;
			}
			if (!party.getLeader().equals(player.getUniqueId())) {
				player.sendMessage(ChatColor.RED + "You are not leader of the party!");
				return true;
			}
			party.setLeader(player.getUniqueId());
		}
		return true;
		case "nation": {
			NationParty party = TownyElections.getInstance().getPartyManager().getPlayerNationParty(player.getUniqueId());
			if (party == null) {
				player.sendMessage(ChatColor.RED + "You are not part of a party");
				return true;
			}
			if (!party.getLeader().equals(player.getUniqueId())) {
				player.sendMessage(ChatColor.RED + "You are not leader of the party!");
				return true;
			}
			party.setLeader(player.getUniqueId());
		}
		return true;
		default:
			return executeHelp(sender, cmd, str, args);
		}
	}

	private boolean executePromote(CommandSender sender, Command cmd, String str, String[] args) {
		if (args.length < 3) {
			return notEnoughArguments(sender, cmd, str, args);
		}

		if (!isPlayer(sender)) return true;
		
		Player player = (Player) sender;
		switch (args[1].toLowerCase()) {
		case "town": {
			TownParty party = TownyElections.getInstance().getPartyManager().getPlayerTownParty(player.getUniqueId());
			if (party == null) {
				player.sendMessage(ChatColor.RED + "You are not part of a party");
				return true;
			}
			if (!party.getLeader().equals(player.getUniqueId())) {
				player.sendMessage(ChatColor.RED + "You are not leader of the party!");
				return true;
			}
			if (!party.getMembers().contains(player.getUniqueId())) {
				player.sendMessage(ChatColor.RED + "That player is not a member of the party!");
				return true;
			}
			Player promotedPlayer = Bukkit.getPlayer(args[2]);
			if (promotedPlayer == null) {
				player.sendMessage(ChatColor.RED + "That player does not exist");
				return true;
			}
			party.addAssistant(promotedPlayer.getUniqueId());
			player.sendMessage(ChatColor.GREEN + promotedPlayer.getName() + " was promoted!");
			promotedPlayer.sendMessage(ChatColor.GREEN + "You were promoted by the leader of your party!");
		}
		return true;
		case "nation": {
			NationParty party = TownyElections.getInstance().getPartyManager().getPlayerNationParty(player.getUniqueId());
			if (party == null) {
				player.sendMessage(ChatColor.RED + "You are not part of a party");
				return true;
			}
			if (!party.getLeader().equals(player.getUniqueId())) {
				player.sendMessage(ChatColor.RED + "You are not leader of the party!");
				return true;
			}
			if (!party.getMembers().contains(player.getUniqueId())) {
				player.sendMessage(ChatColor.RED + "That player is not a member of the party!");
				return true;
			}
			Player promotedPlayer = Bukkit.getPlayer(args[2]);
			if (promotedPlayer == null) {
				player.sendMessage(ChatColor.RED + "That player does not exist");
				return true;
			}
			party.addAssistant(promotedPlayer.getUniqueId());
			player.sendMessage(ChatColor.GREEN + promotedPlayer.getName() + " was promoted!");
			promotedPlayer.sendMessage(ChatColor.GREEN + "You were promoted by the leader of your party!");
		}
		return true;
		default:
			return executeHelp(sender, cmd, str, args);
		}
	}

	private boolean executeDemote(CommandSender sender, Command cmd, String str, String[] args) {
		if (args.length < 3) {
			return notEnoughArguments(sender, cmd, str, args);
		}

		if (!isPlayer(sender)) return true;
		
		Player player = (Player) sender;
		switch (args[1].toLowerCase()) {
		case "town": {
			TownParty party = TownyElections.getInstance().getPartyManager().getPlayerTownParty(player.getUniqueId());
			if (party == null) {
				player.sendMessage(ChatColor.RED + "You are not part of a party");
				return true;
			}
			if (!party.getLeader().equals(player.getUniqueId())) {
				player.sendMessage(ChatColor.RED + "You are not leader of the party!");
				return true;
			}
			if (!party.getMembers().contains(player.getUniqueId())) {
				player.sendMessage(ChatColor.RED + "That player is not a member of the party!");
				return true;
			}
			Player demotedPlayer = Bukkit.getPlayer(args[2]);
			if (demotedPlayer == null) {
				player.sendMessage(ChatColor.RED + "That player does not exist");
				return true;
			}
			if (party.isAssistant(demotedPlayer.getUniqueId())) {
				player.sendMessage(ChatColor.RED + "That player is not assistant");
				return true;
			}
			party.removeAssistant(demotedPlayer.getUniqueId());
			player.sendMessage(ChatColor.GREEN + demotedPlayer.getName() + " was demoted");
			demotedPlayer.sendMessage(ChatColor.GREEN + "You were demoted by the leader of your party!");
		}
		return true;
		case "nation": {
			NationParty party = TownyElections.getInstance().getPartyManager().getPlayerNationParty(player.getUniqueId());
			if (party == null) {
				player.sendMessage(ChatColor.RED + "You are not part of a party");
				return true;
			}
			if (!party.getLeader().equals(player.getUniqueId())) {
				player.sendMessage(ChatColor.RED + "You are not leader of the party!");
				return true;
			}
			if (!party.getMembers().contains(player.getUniqueId())) {
				player.sendMessage(ChatColor.RED + "That player is not a member of the party!");
				return true;
			}
			Player demotedPlayer = Bukkit.getPlayer(args[2]);
			if (demotedPlayer == null) {
				player.sendMessage(ChatColor.RED + "That player does not exist");
				return true;
			}
			if (party.isAssistant(demotedPlayer.getUniqueId())) {
				player.sendMessage(ChatColor.RED + "That player is not assistant");
				return true;
			}
			party.removeAssistant(demotedPlayer.getUniqueId());
			player.sendMessage(ChatColor.GREEN + demotedPlayer.getName() + " was demoted");
			demotedPlayer.sendMessage(ChatColor.GREEN + "You were demoted by the leader of your party!");
		}
		return true;
		default:
			return executeHelp(sender, cmd, str, args);
		}
	}

	private boolean executeInfo(CommandSender sender, Command cmd, String str, String[] args) {
		if (args.length < 2) {
			return notEnoughArguments(sender, cmd, str, args);
		}

		if (!isPlayer(sender)) return true;
		
		Player player = (Player) sender;
		switch (args[1].toLowerCase()) {
		case "town": {
			TownParty party = TownyElections.getInstance().getPartyManager().getPlayerTownParty(player.getUniqueId());
			if (party == null) {
				player.sendMessage(ChatColor.RED + "You are not part of a party");
				return true;
			}
			StringBuilder builder = new StringBuilder();
			builder.append(ChatColor.GOLD + "" + ChatColor.BOLD + party.getName() + "\n" + ChatColor.RESET);
			builder.append(ChatColor.GREEN + "  Leader: " + Bukkit.getOfflinePlayer(party.getLeader()).getName() + "\n");
			builder.append(ChatColor.GREEN + "  Assistants: ");
			for (UUID assistant : party.getAssistants()) {
				builder.append(Bukkit.getOfflinePlayer(assistant) + " ");
			}
			builder.append("\n");
			builder.append(ChatColor.GREEN + "  Members: ");
			for (UUID member : party.getMembers()) {
				builder.append(Bukkit.getOfflinePlayer(member) + " ");
			}
			player.sendMessage(builder.toString());
		}
		return true;
		case "nation": {
			NationParty party = TownyElections.getInstance().getPartyManager().getPlayerNationParty(player.getUniqueId());
			if (party == null) {
				player.sendMessage(ChatColor.RED + "You are not part of a party");
				return true;
			}
			StringBuilder builder = new StringBuilder();
			builder.append(ChatColor.GOLD + "" + ChatColor.BOLD + party.getName() + "\n" + ChatColor.RESET);
			builder.append(ChatColor.GREEN + "  Leader: " + Bukkit.getOfflinePlayer(party.getLeader()).getName() + "\n");
			builder.append(ChatColor.GREEN + "  Assistants: ");
			for (UUID assistant : party.getAssistants()) {
				builder.append(Bukkit.getOfflinePlayer(assistant) + " ");
			}
			builder.append("\n");
			builder.append(ChatColor.GREEN + "  Members: ");
			for (UUID member : party.getMembers()) {
				builder.append(Bukkit.getOfflinePlayer(member) + " ");
			}
			player.sendMessage(builder.toString());
		}
		return true;
		default:
			return executeHelp(sender, cmd, str, args);
		}
	}

	private boolean notEnoughArguments(CommandSender sender, Command cmd, String str, String[] args) {
		sender.sendMessage(ChatColor.RED + "Not enough arguments!");
		return true;
	}

}

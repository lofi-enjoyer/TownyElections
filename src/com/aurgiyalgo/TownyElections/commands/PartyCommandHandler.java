package com.aurgiyalgo.TownyElections.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.aurgiyalgo.TownyElections.parties.PartyManager;
import com.palmergames.bukkit.towny.Towny;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
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

import static com.aurgiyalgo.TownyElections.TownyElections.getMessage;

public class PartyCommandHandler implements CommandExecutor {

	private PartyManager instance;

	public PartyCommandHandler() {
		this.instance = TownyElections.getInstance().getPartyManager();
	}

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
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', TownyElections.Text.PARTY_HELP_MESSAGE));
		return true;
	}

	private boolean executeCreate(CommandSender sender, Command cmd, String str, String[] args) {
		if (args.length < 3) {
			return notEnoughArguments(sender);
		}

		if (!isPlayer(sender)) return true;
		
		Player player = (Player) sender;
		switch (args[1].toLowerCase()) {
		case "town": {
			if (!player.hasPermission(TownyElections.Permissions.TOWNPARTY_CREATE)) {
				player.sendMessage(getMessage("no-permission"));
				return true;
			}
			TownParty party = instance.getPlayerTownParty(player.getUniqueId());
			if (party != null) {
				player.sendMessage(getMessage("already-part-of-a-party"));
				return true;
			}
			Town town;
			try {
				town = TownyUniverse.getInstance().getDataSource().getResident(player.getName()).getTown();
			} catch (NotRegisteredException e) {
				player.sendMessage(TownyElections.getMessage("not-in-a-town"));
				return true;
			}
			if (instance.getPartiesForTown(town.getName()).stream().filter(prty -> prty.getName().toLowerCase().equals(args[2].toLowerCase())).collect(Collectors.toList()).size() > 0) {
				player.sendMessage(TownyElections.getMessage("name-taken"));
				return true;
			}
			party = new TownParty(args[2], player.getUniqueId(), town.getUuid());
			instance.addParty(party);
			player.sendMessage(TownyElections.getMessage("party-created"));
		}
		return true;
		case "nation": {
			if (!player.hasPermission(TownyElections.Permissions.NATIONPARTY_CREATE)) {
				player.sendMessage(getMessage("no-permission"));
				return true;
			}
			NationParty party = instance.getPlayerNationParty(player.getUniqueId());
			if (party != null) {
				player.sendMessage(getMessage("already-in-a-party"));
				return true;
			}
			Nation nation;
			try {
				nation = TownyUniverse.getInstance().getDataSource().getResident(player.getName()).getTown().getNation();
			} catch (NotRegisteredException e) {
				player.sendMessage(getMessage("not-in-a-nation"));
				return true;
			}
			if (instance.getPartiesForNation(nation.getName()).stream().filter(prty -> prty.getName().toLowerCase().equals(args[2].toLowerCase())).collect(Collectors.toList()).size() > 0) {
				player.sendMessage(getMessage("name-taken"));
				return true;
			}
			party = new NationParty(args[2], player.getUniqueId(), nation.getUuid());
			instance.addParty(party);
			player.sendMessage(getMessage("party-created"));
		}
		return true;
		default:
			return executeHelp(sender, cmd, str, args);
		}
	}

	private boolean executeLeave(CommandSender sender, Command cmd, String str, String[] args) {
		if (args.length < 2) {
			return notEnoughArguments(sender);
		}
		
		if (!isPlayer(sender)) return true;
		
		Player player = (Player) sender;
		Party party;

		switch (args[1].toLowerCase()) {

			case "town": {
				if (!TownyElections.hasPerms(player, TownyElections.Permissions.TOWNPARTY_LEAVE)) return true;
				party = instance.getPlayerTownParty(player.getUniqueId());
			} break;

			case "nation": {
				if (!TownyElections.hasPerms(player, TownyElections.Permissions.NATIONPARTY_LEAVE)) return true;
				party = instance.getPlayerNationParty(player.getUniqueId());
			} break;

			default:
				return executeHelp(sender, cmd, str, args);

		}

		if (party == null) {
			player.sendMessage(getMessage("not-in-a-party"));
			return true;
		}

		if (party.getLeader().equals(player.getUniqueId())) {
			player.sendMessage(getMessage("leader-cannot-leave"));
			return true;
		}

		party.removeMember(player.getUniqueId());
		return true;
	}

	private boolean executeAdd(CommandSender sender, Command cmd, String str, String[] args) {
		if (args.length < 3) {
			return notEnoughArguments(sender);
		}
		
		if (!isPlayer(sender)) return true;
		
		Player player = (Player) sender;
		Party party;

		switch (args[1].toLowerCase()) {

			case "town": {
				if (!TownyElections.hasPerms(player, TownyElections.Permissions.TOWNPARTY_ADD)) return true;
				party = instance.getPlayerTownParty(player.getUniqueId());
			} break;

			case "nation": {
				if (!TownyElections.hasPerms(player, TownyElections.Permissions.NATIONPARTY_ADD)) return true;
				party = instance.getPlayerNationParty(player.getUniqueId());
			} break;

			default:
				return executeHelp(sender, cmd, str, args);

		}

		if (party == null) {
			player.sendMessage(getMessage("not-in-a-party"));
			return true;
		}

		if (!party.getLeader().equals(player.getUniqueId()) && !party.isAssistant(player.getUniqueId())) {
			player.sendMessage(getMessage("no-permission"));
			return true;
		}

		Player invitedPlayer = Bukkit.getPlayer(args[2]);
		if (invitedPlayer == null) {
			player.sendMessage(getMessage("player-not-online"));
			return true;
		}

		if (party.isInvited(invitedPlayer.getUniqueId())) {
			player.sendMessage(getMessage("already-invited"));
			return true;
		}

		party.addInvite(invitedPlayer.getUniqueId());
		player.sendMessage(getMessage("player-invited").replace("%player%", invitedPlayer.getName()));
		invitedPlayer.sendMessage(getMessage("invited-to-party").replace("%party%", party.getName()));

		return true;
	}

	private boolean executeAccept(CommandSender sender, Command cmd, String str, String[] args) {
		if (args.length < 3) {
			return notEnoughArguments(sender);
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

		Party party;

		switch (args[1].toLowerCase()) {

			case "town": {

				if (!TownyElections.hasPerms(player, TownyElections.Permissions.TOWNPARTY_ACCEPT)) return true;

				try {
					party = instance.getPartiesForTown(resident.getTown().getName()).stream().filter(obj -> obj.getName().toLowerCase().equals(args[2])).collect(Collectors.toList()).get(0);
				} catch (Exception e) {
					player.sendMessage(getMessage("not-in-a-nation"));
					return true;
				}

			} break;

			case "nation": {

				if (!TownyElections.hasPerms(player, TownyElections.Permissions.NATIONPARTY_ACCEPT)) return true;

				try {
					party = instance.getPartiesForNation(resident.getTown().getNation().getName()).stream().filter(obj -> obj.getName().toLowerCase().equals(args[2])).collect(Collectors.toList()).get(0);
				} catch (Exception e) {
					player.sendMessage(getMessage("not-in-a-nation"));
					return true;
				}

			} break;

			default:
				return executeHelp(sender, cmd, str, args);

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

		for (Player iteratedPlayer : Bukkit.getOnlinePlayers()) {
			if (party.getMembers().contains(iteratedPlayer.getUniqueId()))
				iteratedPlayer.sendMessage(getMessage("player-joined-the-party").replace("%player%", player.getName()));
		}

		return true;
	}

	private boolean executeInvites(CommandSender sender, Command cmd, String str, String[] args) {
		if (args.length < 2) {
			return notEnoughArguments(sender);
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

		List<String> parties = new ArrayList<String>();

		switch (args[1].toLowerCase()) {

			case "town": {

				if (!TownyElections.hasPerms(player, TownyElections.Permissions.TOWNPARTY_INVITES)) return true;

				try {
					for (Party p : instance.getPartiesForTown(resident.getTown().getName()))
						if (p.isInvited(player.getUniqueId())) parties.add(p.getName());
				} catch (NotRegisteredException e) {
					player.sendMessage(getMessage("not-in-a-town"));
					return true;
				}

			} break;

			case "nation": {

				if (!TownyElections.hasPerms(player, TownyElections.Permissions.NATIONPARTY_INVITES)) return true;

				try {
					for (Party p : instance.getPartiesForNation(resident.getTown().getNation().getName()))
						if (p.isInvited(player.getUniqueId())) parties.add(p.getName());
				} catch (NotRegisteredException e) {
					player.sendMessage(getMessage("not-in-a-nation"));
					return true;
				}

			} break;

			default:
				return executeHelp(sender, cmd, str, args);

		}

		if (parties.size() <= 0) {
			player.sendMessage(getMessage("no-invitations"));
			return true;
		}

		StringBuilder builder = new StringBuilder();
		builder.append(getMessage("invitations"));
		builder.append(parties.get(0));

		for (int i = 1; i < parties.size(); i++) {
			builder.append(", ");
			builder.append(parties.get(i));
		}

		player.sendMessage(builder.toString());

		return true;
	}

	private boolean executeSetLeader(CommandSender sender, Command cmd, String str, String[] args) {
		if (args.length < 3) {
			return notEnoughArguments(sender);
		}

		if (!isPlayer(sender)) return true;
		
		Player player = (Player) sender;
		Party party;

		switch (args[1].toLowerCase()) {

			case "town": {
				if (!TownyElections.hasPerms(player, TownyElections.Permissions.TOWNPARTY_SETLEADER)) return true;
				party = instance.getPlayerTownParty(player.getUniqueId());
			} break;

			case "nation": {
				if (!TownyElections.hasPerms(player, TownyElections.Permissions.NATIONPARTY_SETLEADER)) return true;
				party = instance.getPlayerNationParty(player.getUniqueId());
			} break;

			default:
				return executeHelp(sender, cmd, str, args);

		}

		if (party == null) {
			player.sendMessage(getMessage("not-in-a-party"));
			return true;
		}

		if (!party.getLeader().equals(player.getUniqueId())) {
			player.sendMessage(getMessage("not-leader"));
			return true;
		}

		party.setLeader(player.getUniqueId());

		return true;
	}

	private boolean executePromote(CommandSender sender, Command cmd, String str, String[] args) {
		if (args.length < 3) {
			return notEnoughArguments(sender);
		}

		if (!isPlayer(sender)) return true;
		
		Player player = (Player) sender;
		Party party;

		switch (args[1].toLowerCase()) {

			case "town": {
				if (!TownyElections.hasPerms(player, TownyElections.Permissions.TOWNPARTY_PROMOTE)) return true;
				party = instance.getPlayerTownParty(player.getUniqueId());
			} break;

			case "nation": {
				if (!TownyElections.hasPerms(player, TownyElections.Permissions.NATIONPARTY_PROMOTE)) return true;
				party = instance.getPlayerNationParty(player.getUniqueId());
			} break;

			default:
				return executeHelp(sender, cmd, str, args);

		}

		if (party == null) {
			player.sendMessage(getMessage("not-in-a-party"));
			return true;
		}

		if (!party.getLeader().equals(player.getUniqueId())) {
			player.sendMessage(getMessage("not-leader"));
			return true;
		}

		if (!party.getMembers().contains(player.getUniqueId())) {
			player.sendMessage(getMessage("not-a-member"));
			return true;
		}

		OfflinePlayer promotedPlayer = Bukkit.getOfflinePlayer(args[2]);
		if (party.isAssistant(promotedPlayer.getUniqueId())) {
			player.sendMessage(getMessage("already-assistant"));
			return true;
		}
		party.addAssistant(promotedPlayer.getUniqueId());

		player.sendMessage(getMessage("player-promoted"));

		if (promotedPlayer.isOnline())
			promotedPlayer.getPlayer().sendMessage(getMessage("you-were-promoted"));

		return true;
	}

	private boolean executeDemote(CommandSender sender, Command cmd, String str, String[] args) {
		if (args.length < 3) {
			return notEnoughArguments(sender);
		}

		if (!isPlayer(sender)) return true;
		
		Player player = (Player) sender;
		Party party;

		switch (args[1].toLowerCase()) {

			case "town": {
				if (!TownyElections.hasPerms(player, TownyElections.Permissions.TOWNPARTY_DEMOTE)) return true;
				party = instance.getPlayerTownParty(player.getUniqueId());
			} break;

			case "nation": {
				if (!TownyElections.hasPerms(player, TownyElections.Permissions.NATIONPARTY_DEMOTE)) return true;
				party = instance.getPlayerNationParty(player.getUniqueId()); } break;

			default:
				return executeHelp(sender, cmd, str, args);

		}

		if (party == null) {
			player.sendMessage(getMessage("not-in-a-party"));
			return true;
		}

		if (!party.getLeader().equals(player.getUniqueId())) {
			player.sendMessage(getMessage("not-leader"));
			return true;
		}

		if (!party.getMembers().contains(player.getUniqueId())) {
			player.sendMessage(getMessage("not-a-member"));
			return true;
		}

		OfflinePlayer demotedPlayer = Bukkit.getOfflinePlayer(args[2]);
		if (party.isAssistant(demotedPlayer.getUniqueId())) {
			player.sendMessage(getMessage("not-assistant"));
			return true;
		}

		party.removeAssistant(demotedPlayer.getUniqueId());

		player.sendMessage(getMessage("player-was-demoted").replace("%player%", demotedPlayer.getName()));

		if (demotedPlayer.isOnline())
			demotedPlayer.getPlayer().sendMessage(getMessage("you-were-demoted"));

		return true;
	}

	private boolean executeInfo(CommandSender sender, Command cmd, String str, String[] args) {
		if (args.length < 2) {
			return notEnoughArguments(sender);
		}

		if (!isPlayer(sender)) return true;
		
		Player player = (Player) sender;

		Party party;
		switch (args[1].toLowerCase()) {
		case "town": {
			if (!TownyElections.hasPerms(player, TownyElections.Permissions.TOWNPARTY_INFO)) return true;
			party = instance.getPlayerTownParty(player.getUniqueId());
		} break;
		case "nation": {
			if (!TownyElections.hasPerms(player, TownyElections.Permissions.NATIONPARTY_INFO)) return true;
			party = instance.getPlayerNationParty(player.getUniqueId());
		} break;
		default:
			return executeHelp(sender, cmd, str, args);
		}

		if (party == null) {
			player.sendMessage(getMessage("not-in-a-party"));
			return true;
		}

		StringBuilder builder = new StringBuilder();
		builder.append(ChatColor.GOLD + "" + ChatColor.BOLD + party.getName() + "\n" + ChatColor.RESET);
		builder.append(getMessage("leader"));
		builder.append(Bukkit.getOfflinePlayer(party.getLeader()).getName());
		builder.append("\n");
		builder.append(getMessage("assistants"));
		if (party.getAssistants().size() != 0) {
			builder.append(party.getAssistants().get(0));
			for (int i = 1; i < party.getAssistants().size(); i++) {
				builder.append(", ");
				builder.append(Bukkit.getOfflinePlayer(party.getAssistants().get(0)).getName());
			}
		}
		builder.append("\n");
		builder.append(getMessage("members"));
		builder.append(party.getMembers().get(0));
		for (int i = 1; i < party.getMembers().size(); i++) {
			builder.append(", ");
			builder.append(Bukkit.getOfflinePlayer(party.getMembers().get(i)).getName());
		}
		player.sendMessage(builder.toString());
		return true;
	}

	private boolean notEnoughArguments(CommandSender sender) {
		sender.sendMessage(getMessage("not-enough-arguments"));
		return true;
	}

	private boolean isPlayer(CommandSender sender) {
		if (sender instanceof Player) return true;
		sender.sendMessage(getMessage("only-player"));
		return false;
	}

}

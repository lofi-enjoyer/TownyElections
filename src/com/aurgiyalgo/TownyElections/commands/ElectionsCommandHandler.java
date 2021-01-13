package com.aurgiyalgo.TownyElections.commands;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.aurgiyalgo.TownyElections.TownyElections;
import com.aurgiyalgo.TownyElections.elections.NationElection;
import com.aurgiyalgo.TownyElections.elections.TownElection;
import com.aurgiyalgo.TownyElections.parties.NationParty;
import com.aurgiyalgo.TownyElections.parties.Party;
import com.aurgiyalgo.TownyElections.parties.TownParty;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;

public class ElectionsCommandHandler implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args) {
		if (args.length < 1) {
			return executeHelp(sender, cmd, str, args);
		}
		switch (args[0]) {
		case "town":
			if (args.length < 2) {
				sender.sendMessage(TownyElections.getTranslatedMessage("not-enough-arguments"));
				return true;
			}
			switch (args[1]) {
			case "vote":
				return executeTownVote(sender, cmd, str, args);
			case "convoke":
				return executeTownConvoke(sender, cmd, str, args);
			case "list":
				return executeTownList(sender, cmd, str, args);
			case "stop":
				return executeTownStop(sender, cmd, str, args);
			case "unvote":
				return executeTownUnvote(sender, cmd, str, args);
			default:
				sender.sendMessage(ChatColor.RED + "Invalid argument!");
				break;
			}
			break;
		case "nation":
			if (args.length < 2) {
				sender.sendMessage(TownyElections.getTranslatedMessage("not-enough-arguments"));
				return true;
			}
			switch (args[1]) {
			case "vote":
				return executeNationVote(sender, cmd, str, args);
			case "convoke":
				return executeNationConvoke(sender, cmd, str, args);
			case "list":
				return executeNationList(sender, cmd, str, args);
			case "stop":
				return executeNationStop(sender, cmd, str, args);
			case "unvote":
				return executeNationUnvote(sender, cmd, str, args);
			default:
				sender.sendMessage(ChatColor.RED + "Invalid argument!");
				break;
			}
			break;
		default:
			sender.sendMessage(ChatColor.RED + "Invalid argument!");
			break;
		}
		return true;
	}

	private boolean executeTownUnvote(CommandSender sender, Command cmd, String str, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(TownyElections.getTranslatedMessage("only-player"));
			return true;
		}
		Player p = (Player) sender;
		if (!TownyElections.hasPerms(p, TownyElections.Permissions.TOWN_UNVOTE)) return true;
		try {
			TownyUniverse.getInstance().getDataSource().getResident(p.getName()).getTown();
		} catch (NotRegisteredException e) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-in-a-town"));
			return true;
		}
		TownElection e = TownyElections.getInstance().getElectionManager().getTownElection(p);
		if (e == null) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-active-election"));
			return true;
		}
		e.removeVote(p.getUniqueId());
		p.sendMessage(TownyElections.getTranslatedMessage("unvoted"));
		return true;
	}

	private boolean executeNationUnvote(CommandSender sender, Command cmd, String str, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(TownyElections.getTranslatedMessage("only-player"));
			return true;
		}
		Player p = (Player) sender;
		if (!TownyElections.hasPerms(p, TownyElections.Permissions.NATION_UNVOTE)) return true;
		try {
			TownyUniverse.getInstance().getDataSource().getResident(p.getName()).getTown().getNation();
		} catch (NotRegisteredException e) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-in-a-nation"));
			return true;
		}
		NationElection e = TownyElections.getInstance().getElectionManager().getNationElection(p);
		if (e == null) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-active-election-nation"));
			return true;
		}
		e.removeVote(p.getUniqueId());
		return true;
	}

	private boolean executeTownStop(CommandSender sender, Command cmd, String str, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(TownyElections.getTranslatedMessage("only-player"));
			return true;
		}
		Player p = (Player) sender;
		if (!TownyElections.hasPerms(p, TownyElections.Permissions.TOWN_STOP)) return true;
		try {
			TownyUniverse.getInstance().getDataSource().getResident(p.getName()).getTown();
		} catch (NotRegisteredException e) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-in-a-town"));
			return true;
		}
		TownElection e = TownyElections.getInstance().getElectionManager().getTownElection(p);
		if (e == null) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-active-election"));
			return true;
		}
		TownyElections.getInstance().getElectionManager().removeTownElection(e);
		return true;
	}

	private boolean executeNationStop(CommandSender sender, Command cmd, String str, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(TownyElections.getTranslatedMessage("only-player"));
			return true;
		}
		Player p = (Player) sender;
		if (!TownyElections.hasPerms(p, TownyElections.Permissions.NATION_STOP)) return true;
		try {
			TownyUniverse.getInstance().getDataSource().getResident(p.getName()).getTown().getNation();
		} catch (NotRegisteredException e) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-in-a-nation"));
			return true;
		}
		NationElection e = TownyElections.getInstance().getElectionManager().getNationElection(p);
		if (e == null) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-active-election-nation"));
			return true;
		}
		TownyElections.getInstance().getElectionManager().removeNationElection(e);
		return true;
	}

	private boolean executeTownList(CommandSender sender, Command cmd, String str, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(TownyElections.getTranslatedMessage("only-player"));
			return true;
		}
		Player p = (Player) sender;
		if (!TownyElections.hasPerms(p, TownyElections.Permissions.TOWN_LIST)) return true;
		TownElection e = TownyElections.getInstance().getElectionManager().getTownElection(p);
		if (e == null) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-active-election"));
			return true;
		}
		p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 1);
		StringBuilder builder = new StringBuilder();
		builder.append(ChatColor.GOLD + "Candidates:\n");
		List<TownParty> parties = TownyElections.getInstance().getPartyManager().getPartiesForTown(e.getTown().getName());
		if (parties.size() <= 0) {
			builder.append(ChatColor.RED + "There are no candidates");
			p.sendMessage(builder.toString());
			return true;
		}
		for (Party party : parties) {
			builder.append("- " + party.getName());
		}
		p.sendMessage(builder.toString());
		p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 1);
		return true;
	}

	private boolean executeNationList(CommandSender sender, Command cmd, String str, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(TownyElections.getTranslatedMessage("only-player"));
			return true;
		}
		Player p = (Player) sender;
		if (!TownyElections.hasPerms(p, TownyElections.Permissions.NATION_LIST)) return true;
		NationElection e = TownyElections.getInstance().getElectionManager().getNationElection(p);
		if (e == null) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-active-election-nation"));
			return true;
		}
		StringBuilder builder = new StringBuilder();
		builder.append(ChatColor.GOLD + "Candidates:\n");
		List<NationParty> parties = TownyElections.getInstance().getPartyManager().getPartiesForNation(e.getNation().getName());
		if (parties.size() <= 0) {
			builder.append(ChatColor.RED + "There are no candidates");
			p.sendMessage(builder.toString());
			return true;
		}
		for (Party party : parties) {
			builder.append("- " + party.getName());
		}
		p.sendMessage(builder.toString());
		p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 1);
		return true;
	}

	private boolean executeTownConvoke(CommandSender sender, Command cmd, String str, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(TownyElections.getTranslatedMessage("only-player"));
			return true;
		}
		Player p = (Player) sender;
		if (!TownyElections.hasPerms(p, TownyElections.Permissions.TOWN_CONVOKE)) return true;
		Town t;
		try {
			t = TownyUniverse.getInstance().getDataSource().getResident(p.getName()).getTown();
		} catch (NotRegisteredException e) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-in-a-town"));
			return true;
		}
		if (args.length < 2) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-enough-arguments"));
			return true;
		}
		if (TownyElections.getInstance().getElectionManager().getTownElection(p) != null) {
			p.sendMessage(TownyElections.getTranslatedMessage("active-election"));
			return true;
		}
		long finishTime = 0;
		try {
			finishTime = Integer.parseInt(args[2]) * 1000 + System.currentTimeMillis();
		} catch (Exception e) {
			p.sendMessage(TownyElections.getTranslatedMessage("error-input-string"));
			return true;
		}
		TownElection e = new TownElection(finishTime, t);
		TownyElections.getInstance().getElectionManager().addTownElection(e);
		TownyElections.sendTownSubtitle(t, TownyElections.getTranslatedMessage("election-convoked"));
		p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 1);
		return true;
	}

	private boolean executeNationConvoke(CommandSender sender, Command cmd, String str, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(TownyElections.getTranslatedMessage("only-player"));
			return true;
		}
		Player p = (Player) sender;
		if (!TownyElections.hasPerms(p, TownyElections.Permissions.NATION_CONVOKE)) return true;
		Nation n;
		try {
			n = TownyUniverse.getInstance().getDataSource().getResident(p.getName()).getTown().getNation();
		} catch (NotRegisteredException e) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-in-a-nation"));
			return true;
		}
		if (args.length < 2) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-enough-arguments"));
			return true;
		}
		if (TownyElections.getInstance().getElectionManager().getTownElection(p) != null) {
			p.sendMessage(TownyElections.getTranslatedMessage("active-election"));
		}
		long finishTime = 0;
		try {
			finishTime = Integer.parseInt(args[2]) * 1000 + System.currentTimeMillis();
		} catch (Exception e) {
			p.sendMessage(TownyElections.getTranslatedMessage("error-input-string"));
			return true;
		}
		NationElection e = new NationElection(n, finishTime);
		TownyElections.getInstance().getElectionManager().addNationElection(e);
		TownyElections.sendNationSubtitle(n, TownyElections.getTranslatedMessage("election-convoked"));
		p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 1);
		return true;
	}

	private boolean executeTownVote(CommandSender sender, Command cmd, String str, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(TownyElections.getTranslatedMessage("only-player"));
			return true;
		}
		Player p = (Player) sender;
		if (!TownyElections.hasPerms(p, TownyElections.Permissions.TOWN_VOTE)) return true;
		if (args.length <= 2) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-enough-arguments"));
			return true;
		}
		TownElection e;
		e = TownyElections.getInstance().getElectionManager().getTownElection(p);
		if (e == null) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-active-election"));
			return true;
		}
		if (e.hasVoted(p.getUniqueId())) {
			p.sendMessage(TownyElections.getTranslatedMessage("already-voted"));
			return true;
		}
		if (TownyElections.getInstance().getPartyManager().getPartiesForTown(e.getTown().getName()).stream().filter(party -> party.getName().equals(args[2])).collect(Collectors.toList()).isEmpty()) {
			p.sendMessage(TownyElections.getTranslatedMessage("invalid-candidate"));
			return true;
		}
		if (System.currentTimeMillis() >= e.getEndTime()) {
			TownyElections.getInstance().getElectionManager().removeTownElection(e);
			return true;
		}
//		VoteGui.INVENTORY.open(p);
		e.addVote(p.getUniqueId(), args[2]);
		String msg = TownyElections.getTranslatedMessage("you-voted");
		msg = msg.replaceAll("%party%", args[2]);
		p.sendMessage(msg);
		p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 1);
		return true;
	}

	private boolean executeNationVote(CommandSender sender, Command cmd, String str, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(TownyElections.getTranslatedMessage("only-player"));
			return true;
		}
		Player p = (Player) sender;
		if (!TownyElections.hasPerms(p, TownyElections.Permissions.NATION_VOTE)) return true;
		if (args.length <= 2) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-enough-arguments"));
			return true;
		}
		NationElection e;
		e = TownyElections.getInstance().getElectionManager().getNationElection(p);
		if (e == null) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-active-election-nation"));
			return true;
		}
		if (e.hasVoted(p.getUniqueId())) {
			p.sendMessage(TownyElections.getTranslatedMessage("already-voted"));
			return true;
		}
		if (TownyElections.getInstance().getPartyManager().getPartiesForNation(e.getNation().getName()).stream().filter(party -> party.getName().equals(args[2])).collect(Collectors.toList()).isEmpty()) {
			p.sendMessage(TownyElections.getTranslatedMessage("invalid-candidate"));
			return true;
		}
		if (System.currentTimeMillis() >= e.getEndTime()) {
			TownyElections.getInstance().getElectionManager().removeNationElection(e);
			return true;
		}
		e.addVote(p.getUniqueId(), args[2]);
		String msg = TownyElections.getTranslatedMessage("you-voted");
		msg = msg.replaceAll("%party%", args[2]);
		p.sendMessage(msg);
		p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 1);
		return true;
	}

	private boolean executeHelp(CommandSender sender, Command cmd, String str, String[] args) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', TownyElections.Text.HELP_MESSAGE));
		return true;
	}

}

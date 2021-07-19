package com.aurgiyalgo.TownyElections.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.aurgiyalgo.TownyElections.gui.NationVoteGui;
import com.aurgiyalgo.TownyElections.gui.TownStopGui;
import com.aurgiyalgo.TownyElections.gui.TownVoteGui;
import com.aurgiyalgo.TownyElections.gui.VoteGui;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.aurgiyalgo.TownyElections.TownyElections;
import com.aurgiyalgo.TownyElections.TownyElections.Configuration;
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

	private List<SubCommand> subCommands;

	public ElectionsCommandHandler() {
		this.subCommands = new ArrayList<>();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args) {
		if (args.length < 1) {
			return executeHelp(sender, cmd, str, args);
		}
		if (args.length < 2) {
			sender.sendMessage(TownyElections.getMessage("not-enough-arguments"));
			return true;
		}
		switch (args[0]) {
		case "vote":
			return executeVote(sender, cmd, str, args);
		case "convoke":
			return executeConvoke(sender, cmd, str, args);
		case "list":
			return executeList(sender, cmd, str, args);
		case "stop":
			return executeStop(sender, cmd, str, args);
		case "unvote":
			return executeUnvote(sender, cmd, str, args);
		default:
			sender.sendMessage(ChatColor.RED + "Invalid argument!");
			break;
		}
		return true;
	}

	private boolean executeVote(CommandSender sender, Command cmd, String str, String[] args) {
		switch (args[1]) {
		case "town": return executeTownVote(sender, cmd, str, args);
		case "nation": return executeNationVote(sender, cmd, str, args);
		default:
			sender.sendMessage(ChatColor.RED + "Invalid argument!");
			return true;
		}
	}

	private boolean executeConvoke(CommandSender sender, Command cmd, String str, String[] args) {
		switch (args[1]) {
		case "town": return executeTownConvoke(sender, cmd, str, args);
		case "nation": return executeNationConvoke(sender, cmd, str, args);
		default:
			sender.sendMessage(ChatColor.RED + "Invalid argument!");
			return true;
		}
	}

	private boolean executeList(CommandSender sender, Command cmd, String str, String[] args) {
		switch (args[1]) {
		case "town": return executeTownList(sender, cmd, str, args);
		case "nation": return executeNationList(sender, cmd, str, args);
		default:
			sender.sendMessage(ChatColor.RED + "Invalid argument!");
			return true;
		}
	}

	private boolean executeStop(CommandSender sender, Command cmd, String str, String[] args) {
		switch (args[1]) {
		case "town": return executeTownStop(sender, cmd, str, args);
		case "nation": return executeNationStop(sender, cmd, str, args);
		default:
			sender.sendMessage(ChatColor.RED + "Invalid argument!");
			return true;
		}
	}

	private boolean executeUnvote(CommandSender sender, Command cmd, String str, String[] args) {
		switch (args[1]) {
		case "town": return executeTownUnvote(sender, cmd, str, args);
		case "nation": return executeNationUnvote(sender, cmd, str, args);
		default:
			sender.sendMessage(ChatColor.RED + "Invalid argument!");
			return true;
		}
	}

	private boolean executeTownUnvote(CommandSender sender, Command cmd, String str, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(TownyElections.getMessage("only-player"));
			return true;
		}
		Player p = (Player) sender;
		if (!TownyElections.hasPerms(p, TownyElections.Permissions.TOWN_UNVOTE)) return true;
		try {
			TownyUniverse.getInstance().getDataSource().getResident(p.getName()).getTown();
		} catch (NotRegisteredException e) {
			p.sendMessage(TownyElections.getMessage("not-in-a-town"));
			return true;
		}
		TownElection e = TownyElections.getInstance().getElectionManager().getTownElection(p);
		if (e == null) {
			p.sendMessage(TownyElections.getMessage("not-active-election"));
			return true;
		}
		e.removeVote(p.getUniqueId());
		p.sendMessage(TownyElections.getMessage("unvoted"));
		return true;
	}

	private boolean executeNationUnvote(CommandSender sender, Command cmd, String str, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(TownyElections.getMessage("only-player"));
			return true;
		}
		Player p = (Player) sender;
		if (!TownyElections.hasPerms(p, TownyElections.Permissions.NATION_UNVOTE)) return true;
		try {
			TownyUniverse.getInstance().getDataSource().getResident(p.getName()).getTown().getNation();
		} catch (NotRegisteredException e) {
			p.sendMessage(TownyElections.getMessage("not-in-a-nation"));
			return true;
		}
		NationElection e = TownyElections.getInstance().getElectionManager().getNationElection(p);
		if (e == null) {
			p.sendMessage(TownyElections.getMessage("not-active-election-nation"));
			return true;
		}
		e.removeVote(p.getUniqueId());
		return true;
	}

	private boolean executeTownStop(CommandSender sender, Command cmd, String str, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(TownyElections.getMessage("only-player"));
			return true;
		}
		Player p = (Player) sender;
		if (!TownyElections.hasPerms(p, TownyElections.Permissions.TOWN_STOP)) return true;
		try {
			TownyUniverse.getInstance().getDataSource().getResident(p.getName()).getTown();
		} catch (NotRegisteredException e) {
			p.sendMessage(TownyElections.getMessage("not-in-a-town"));
			return true;
		}
		TownElection e = TownyElections.getInstance().getElectionManager().getTownElection(p);
		if (e == null) {
			p.sendMessage(TownyElections.getMessage("not-active-election"));
			return true;
		}
		TownStopGui.INVENTORY.open(p);
		return true;
	}

	private boolean executeNationStop(CommandSender sender, Command cmd, String str, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(TownyElections.getMessage("only-player"));
			return true;
		}
		Player p = (Player) sender;
		if (!TownyElections.hasPerms(p, TownyElections.Permissions.NATION_STOP)) return true;
		try {
			TownyUniverse.getInstance().getDataSource().getResident(p.getName()).getTown().getNation();
		} catch (NotRegisteredException e) {
			p.sendMessage(TownyElections.getMessage("not-in-a-nation"));
			return true;
		}
		NationElection e = TownyElections.getInstance().getElectionManager().getNationElection(p);
		if (e == null) {
			p.sendMessage(TownyElections.getMessage("not-active-election-nation"));
			return true;
		}
		TownyElections.getInstance().getElectionManager().removeNationElection(e);
		return true;
	}

	private boolean executeTownList(CommandSender sender, Command cmd, String str, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(TownyElections.getMessage("only-player"));
			return true;
		}
		Player p = (Player) sender;
		if (!TownyElections.hasPerms(p, TownyElections.Permissions.TOWN_LIST)) return true;
		TownElection e = TownyElections.getInstance().getElectionManager().getTownElection(p);
		if (e == null) {
			p.sendMessage(TownyElections.getMessage("not-active-election"));
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
			sender.sendMessage(TownyElections.getMessage("only-player"));
			return true;
		}
		Player p = (Player) sender;
		if (!TownyElections.hasPerms(p, TownyElections.Permissions.NATION_LIST)) return true;
		NationElection e = TownyElections.getInstance().getElectionManager().getNationElection(p);
		if (e == null) {
			p.sendMessage(TownyElections.getMessage("not-active-election-nation"));
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
			sender.sendMessage(TownyElections.getMessage("only-player"));
			return true;
		}
		Player p = (Player) sender;
		if (!TownyElections.hasPerms(p, TownyElections.Permissions.TOWN_CONVOKE)) return true;
		Town t;
		try {
			t = TownyUniverse.getInstance().getDataSource().getResident(p.getName()).getTown();
		} catch (NotRegisteredException e) {
			p.sendMessage(TownyElections.getMessage("not-in-a-town"));
			return true;
		}
		if (args.length < 2) {
			p.sendMessage(TownyElections.getMessage("not-enough-arguments"));
			return true;
		}
		if (TownyElections.getInstance().getElectionManager().getTownElection(p) != null) {
			p.sendMessage(TownyElections.getMessage("active-election"));
			return true;
		}
		long finishTime = 0;
		try {
			finishTime = Integer.parseInt(args[2]) * 60 * 1000;
		} catch (Exception e) {
			p.sendMessage(TownyElections.getMessage("error-input-string"));
			return true;
		}
		if (finishTime / (1000 * 60) < Configuration.MIN_DURATION) {
			p.sendMessage(TownyElections.getMessage("min-duration").replace("%min%", String.valueOf(Configuration.MIN_DURATION)));
			return true;
		}
		if (finishTime / (1000 * 60) > Configuration.MAX_DURATION) {
			p.sendMessage(TownyElections.getMessage("max-duration").replace("%max%", String.valueOf(Configuration.MAX_DURATION)));
			return true;
		}
		TownElection e = new TownElection(finishTime + System.currentTimeMillis(), t);
		TownyElections.getInstance().getElectionManager().addTownElection(e);
		TownyElections.sendTownSubtitle(t, TownyElections.getMessage("election-convoked"));
		p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 1);
		return true;
	}

	private boolean executeNationConvoke(CommandSender sender, Command cmd, String str, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(TownyElections.getMessage("only-player"));
			return true;
		}
		Player p = (Player) sender;
		if (!TownyElections.hasPerms(p, TownyElections.Permissions.NATION_CONVOKE)) return true;
		Nation n;
		try {
			n = TownyUniverse.getInstance().getDataSource().getResident(p.getName()).getTown().getNation();
		} catch (NotRegisteredException e) {
			p.sendMessage(TownyElections.getMessage("not-in-a-nation"));
			return true;
		}
		if (args.length < 2) {
			p.sendMessage(TownyElections.getMessage("not-enough-arguments"));
			return true;
		}
		if (TownyElections.getInstance().getElectionManager().getTownElection(p) != null) {
			p.sendMessage(TownyElections.getMessage("active-election"));
		}
		long finishTime = 0;
		try {
			finishTime = Integer.parseInt(args[2]) * 60 * 1000;
		} catch (Exception e) {
			p.sendMessage(TownyElections.getMessage("error-input-string"));
			return true;
		}
		if (finishTime / (1000 * 60) < Configuration.MIN_DURATION) {
			p.sendMessage(TownyElections.getMessage("min-duration").replace("%min%", String.valueOf(Configuration.MIN_DURATION)));
			return true;
		}
		if (finishTime / (1000 * 60) > Configuration.MAX_DURATION) {
			p.sendMessage(TownyElections.getMessage("max-duration").replace("%max%", String.valueOf(Configuration.MAX_DURATION)));
			return true;
		}
		NationElection e = new NationElection(n, finishTime + System.currentTimeMillis());
		TownyElections.getInstance().getElectionManager().addNationElection(e);
		TownyElections.sendNationSubtitle(n, TownyElections.getMessage("election-convoked"));
		p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 1);
		return true;
	}

	private boolean executeTownVote(CommandSender sender, Command cmd, String str, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(TownyElections.getMessage("only-player"));
			return true;
		}
		Player p = (Player) sender;
		if (!TownyElections.hasPerms(p, TownyElections.Permissions.TOWN_VOTE)) return true;
		TownElection e;
		e = TownyElections.getInstance().getElectionManager().getTownElection(p);
		if (e == null) {
			p.sendMessage(TownyElections.getMessage("not-active-election"));
			return true;
		}
		if (e.hasVoted(p.getUniqueId())) {
			p.sendMessage(TownyElections.getMessage("already-voted"));
			return true;
		}
		if (System.currentTimeMillis() >= e.getEndTime()) {
			TownyElections.getInstance().getElectionManager().removeTownElection(e);
			return true;
		}
		TownVoteGui.INVENTORY.open(p);
		return true;
	}

	private boolean executeNationVote(CommandSender sender, Command cmd, String str, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(TownyElections.getMessage("only-player"));
			return true;
		}
		Player p = (Player) sender;
		if (!TownyElections.hasPerms(p, TownyElections.Permissions.NATION_VOTE)) return true;
		NationElection e;
		e = TownyElections.getInstance().getElectionManager().getNationElection(p);
		if (e == null) {
			p.sendMessage(TownyElections.getMessage("not-active-election-nation"));
			return true;
		}
		if (e.hasVoted(p.getUniqueId())) {
			p.sendMessage(TownyElections.getMessage("already-voted"));
			return true;
		}
		if (System.currentTimeMillis() >= e.getEndTime()) {
			TownyElections.getInstance().getElectionManager().removeNationElection(e);
			return true;
		}
		NationVoteGui.INVENTORY.open(p);
		return true;
	}

	private boolean executeHelp(CommandSender sender, Command cmd, String str, String[] args) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', TownyElections.Text.ELECTIONS_HELP_MESSAGE));
		return true;
	}

}

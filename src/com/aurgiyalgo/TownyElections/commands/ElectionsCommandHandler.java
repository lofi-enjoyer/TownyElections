package com.aurgiyalgo.TownyElections.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.aurgiyalgo.TownyElections.TownyElections;
import com.aurgiyalgo.TownyElections.elections.NationElection;
import com.aurgiyalgo.TownyElections.elections.TownDecision;
import com.aurgiyalgo.TownyElections.elections.TownElection;
import com.aurgiyalgo.TownyElections.events.NationElectionCreateEvent;
import com.aurgiyalgo.TownyElections.events.NationElectionRunEvent;
import com.aurgiyalgo.TownyElections.events.NationElectionStopEvent;
import com.aurgiyalgo.TownyElections.events.NationElectionUnvoteEvent;
import com.aurgiyalgo.TownyElections.events.NationElectionVoteEvent;
import com.aurgiyalgo.TownyElections.events.TownDecisionCreateEvent;
import com.aurgiyalgo.TownyElections.events.TownDecisionStopEvent;
import com.aurgiyalgo.TownyElections.events.TownDecisionUnvoteEvent;
import com.aurgiyalgo.TownyElections.events.TownDecisionVoteEvent;
import com.aurgiyalgo.TownyElections.events.TownElectionCreateEvent;
import com.aurgiyalgo.TownyElections.events.TownElectionRunEvent;
import com.aurgiyalgo.TownyElections.events.TownElectionStopEvent;
import com.aurgiyalgo.TownyElections.events.TownElectionUnvoteEvent;
import com.aurgiyalgo.TownyElections.events.TownElectionVoteEvent;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;

public class ElectionsCommandHandler implements CommandExecutor {

	private TownyElections instance;

	public ElectionsCommandHandler(TownyElections instance) {
		this.instance = instance;
	}

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
			case "run":
				return executeTownRun(sender, cmd, str, args);
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
			case "run":
				return executeNationRun(sender, cmd, str, args);
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
		case "towndecision":
			if (args.length < 2) {
				sender.sendMessage(TownyElections.getTranslatedMessage("not-enough-arguments"));
				return true;
			}
			switch (args[1]) {
			case "vote":
				return executeTownDecisionVote(sender, cmd, str, args);
			case "convoke":
				return executeTownDecisionConvoke(sender, cmd, str, args);
			case "stop":
				return executeTownDecisionStop(sender, cmd, str, args);
			case "unvote":
				return executeTownDecisionUnvote(sender, cmd, str, args);
			default:
				sender.sendMessage(ChatColor.RED + "Invalid argument!");
			}
			break;
		default:
			sender.sendMessage(ChatColor.RED + "Invalid argument!");
			break;
		}
		return true;
	}

	private boolean executeTownDecisionUnvote(CommandSender sender, Command cmd, String str, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(TownyElections.getTranslatedMessage("only-player"));
			return true;
		}
		Player p = (Player) sender;
		if (!(p.hasPermission("townyelections.unvote.towndecision"))) {
			p.sendMessage(TownyElections.getTranslatedMessage("no-permission"));
			return true;
		}
		try {
			TownyUniverse.getInstance().getDataSource().getResident(p.getName()).getTown();
		} catch (NotRegisteredException e) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-in-a-town"));
			return true;
		}
		TownDecision e = TownyElections.getTownDecision(p);
		if (e == null) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-active-election"));
			return true;
		}
		TownDecisionUnvoteEvent event = new TownDecisionUnvoteEvent(e);
		Bukkit.getPluginManager().callEvent(event);
		if (!event.isCancelled()) {
			e.removeVote(p.getUniqueId());
		}
		return true;
	}

	private boolean executeTownDecisionStop(CommandSender sender, Command cmd, String str, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(TownyElections.getTranslatedMessage("only-player"));
			return true;
		}
		Player p = (Player) sender;
		if (!(p.hasPermission("townyelections.stop.towndecision"))) {
			p.sendMessage(TownyElections.getTranslatedMessage("no-permission"));
			return true;
		}
		try {
			TownyUniverse.getInstance().getDataSource().getResident(p.getName()).getTown();
		} catch (NotRegisteredException e) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-in-a-town"));
			return true;
		}
		TownDecision e = TownyElections.getTownDecision(p);
		if (e == null) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-active-election"));
			return true;
		}
		TownDecisionStopEvent event = new TownDecisionStopEvent(e);
		Bukkit.getPluginManager().callEvent(event);
		if (!event.isCancelled()) {
			TownyElections.removeTownDecision(e);
		}
		return true;
	}

	private boolean executeTownDecisionConvoke(CommandSender sender, Command cmd, String str, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(TownyElections.getTranslatedMessage("only-player"));
			return true;
		}
		Player p = (Player) sender;
		if (!(p.hasPermission("townyelections.convoke.towndecision"))) {
			p.sendMessage(TownyElections.getTranslatedMessage("no-permission"));
			return true;
		}
		Town t;
		try {
			t = TownyUniverse.getInstance().getDataSource().getResident(p.getName()).getTown();
		} catch (NotRegisteredException e) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-in-a-town"));
			return true;
		}
		if (args.length < 4) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-enough-arguments"));
			return true;
		}
		if (TownyElections.getTownElection(p) != null) {
			p.sendMessage(TownyElections.getTranslatedMessage("active-town-decision"));
		}
		long finishTime = 0;
		try {
			finishTime = Integer.parseInt(args[2]) * 1000 + System.currentTimeMillis();
		} catch (Exception e) {
			p.sendMessage(TownyElections.getTranslatedMessage("error-input-string"));
			return true;
		}
		String type;
		switch (args[3]) {
		case "fire":
			type = TownDecision.FIRE;
			break;
		case "explosion":
			type = TownDecision.EXPLOSIONS;
			break;
		case "pvp":
			type = TownDecision.PVP;
			break;
		case "public":
			type = TownDecision.PUBLIC;
			break;
		case "open":
			type = TownDecision.OPEN;
			break;
		default:
			sender.sendMessage(ChatColor.RED + "Invalid argument!");
			return true;
		}
		TownDecision e = new TownDecision(t, finishTime, type);
		TownDecisionCreateEvent event = new TownDecisionCreateEvent(e);
		Bukkit.getPluginManager().callEvent(event);
		if (!event.isCancelled()) {
			TownyElections.addTownDecision(e);
			TownyElections.sendTownSubtitle(t, TownyElections.getTranslatedMessage("election-convoked"));
			p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 1);
		}
		return true;
	}

	private boolean executeTownDecisionVote(CommandSender sender, Command cmd, String str, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(TownyElections.getTranslatedMessage("only-player"));
			return true;
		}
		Player p = (Player) sender;
		if (TownyElections.checkPerms(p, TownyElections.Permissions.TOWN_VOTE)) return true;
		if (args.length <= 2) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-enough-arguments"));
			return true;
		}
		TownDecision e;
		e = TownyElections.getTownDecision(p);
		if (e == null) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-active-town-decision"));
			return true;
		}
		if (e.hasVoted(p.getUniqueId())) {
			p.sendMessage(TownyElections.getTranslatedMessage("already-voted"));
			return true;
		}
		if (System.currentTimeMillis() >= e.getEndTime()) {
			TownyElections.removeTownDecision(e);
			String msg = TownyElections.getTranslatedMessage("election-won").replaceAll("%option%",
					String.valueOf(e.getWinner()));
			TownyElections.sendTownSubtitle(e.getTown(), msg);
			return true;
		}
		TownDecisionVoteEvent event = new TownDecisionVoteEvent(e);
		Bukkit.getPluginManager().callEvent(event);
		if (!event.isCancelled()) {
			e.addVote(p.getUniqueId(), Boolean.valueOf(args[2]));
			String msg = TownyElections.getTranslatedMessage("you-voted");
			msg = msg.replaceAll("%player%", args[2]);
			p.sendMessage(msg);
			p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 1);
		}
		return true;
	}

	private boolean executeTownUnvote(CommandSender sender, Command cmd, String str, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(TownyElections.getTranslatedMessage("only-player"));
			return true;
		}
		Player p = (Player) sender;
		if (!(p.hasPermission("townyelections.unvote.town"))) {
			p.sendMessage(TownyElections.getTranslatedMessage("no-permission"));
			return true;
		}
		try {
			TownyUniverse.getInstance().getDataSource().getResident(p.getName()).getTown();
		} catch (NotRegisteredException e) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-in-a-town"));
			return true;
		}
		TownElection e = TownyElections.getTownElection(p);
		if (e == null) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-active-election"));
			return true;
		}
		TownElectionUnvoteEvent event = new TownElectionUnvoteEvent(e);
		Bukkit.getPluginManager().callEvent(event);
		if (!event.isCancelled()) {
			e.removeVote(p.getUniqueId());
		}
		return true;
	}

	private boolean executeNationUnvote(CommandSender sender, Command cmd, String str, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(TownyElections.getTranslatedMessage("only-player"));
			return true;
		}
		Player p = (Player) sender;
		if (!(p.hasPermission("townyelections.unvote.nation"))) {
			p.sendMessage(TownyElections.getTranslatedMessage("no-permission"));
			return true;
		}
		try {
			TownyUniverse.getInstance().getDataSource().getResident(p.getName()).getTown().getNation();
		} catch (NotRegisteredException e) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-in-a-nation"));
			return true;
		}
		NationElection e = TownyElections.getNationElection(p);
		if (e == null) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-active-election-nation"));
			return true;
		}
		NationElectionUnvoteEvent event = new NationElectionUnvoteEvent(e);
		Bukkit.getPluginManager().callEvent(event);
		if (!event.isCancelled()) {
			e.removeVote(p.getUniqueId());
		}
		return true;
	}

	private boolean executeTownStop(CommandSender sender, Command cmd, String str, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(TownyElections.getTranslatedMessage("only-player"));
			return true;
		}
		Player p = (Player) sender;
		if (!(p.hasPermission("townyelections.stop.town"))) {
			p.sendMessage(TownyElections.getTranslatedMessage("no-permission"));
			return true;
		}
		try {
			TownyUniverse.getInstance().getDataSource().getResident(p.getName()).getTown();
		} catch (NotRegisteredException e) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-in-a-town"));
			return true;
		}
		TownElection e = TownyElections.getTownElection(p);
		if (e == null) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-active-election"));
			return true;
		}
		TownElectionStopEvent event = new TownElectionStopEvent(e);
		Bukkit.getPluginManager().callEvent(event);
		if (!event.isCancelled()) {
			TownyElections.removeTownElection(e);
		}
		return true;
	}

	private boolean executeNationStop(CommandSender sender, Command cmd, String str, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(TownyElections.getTranslatedMessage("only-player"));
			return true;
		}
		Player p = (Player) sender;
		if (!(p.hasPermission("townyelections.stop.nation"))) {
			p.sendMessage(TownyElections.getTranslatedMessage("no-permission"));
			return true;
		}
		try {
			TownyUniverse.getInstance().getDataSource().getResident(p.getName()).getTown().getNation();
		} catch (NotRegisteredException e) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-in-a-nation"));
			return true;
		}
		NationElection e = TownyElections.getNationElection(p);
		if (e == null) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-active-election-nation"));
			return true;
		}
		NationElectionStopEvent event = new NationElectionStopEvent(e);
		Bukkit.getPluginManager().callEvent(event);
		if (!event.isCancelled()) {
			TownyElections.removeNationElection(e);
		}
		return true;
	}

	private boolean executeHelp(CommandSender sender, Command cmd, String str, String[] args) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', instance.getHelpMessage()));
		return true;
	}

	private boolean executeTownList(CommandSender sender, Command cmd, String str, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(TownyElections.getTranslatedMessage("only-player"));
			return true;
		}
		Player p = (Player) sender;
		TownElection e = TownyElections.getTownElection(p);
		if (e == null) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-active-election"));
			return true;
		}
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Candidates: "));
		for (UUID id : e.getCandidates()) {
			p.sendMessage("- " + Bukkit.getOfflinePlayer(id).getName());
		}
		p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 1);
		return true;
	}

	private boolean executeNationList(CommandSender sender, Command cmd, String str, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(TownyElections.getTranslatedMessage("only-player"));
			return true;
		}
		Player p = (Player) sender;
		NationElection e = TownyElections.getNationElection(p);
		if (e == null) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-active-election-nation"));
			return true;
		}
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Candidates: "));
		for (UUID id : e.getCandidates()) {
			p.sendMessage("- " + Bukkit.getOfflinePlayer(id).getName());
		}
		p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 1);
		return true;
	}

	private boolean executeTownConvoke(CommandSender sender, Command cmd, String str, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(TownyElections.getTranslatedMessage("only-player"));
			return true;
		}
		Player p = (Player) sender;
		if (!(p.hasPermission("townyelections.convoke.town"))) {
			p.sendMessage(TownyElections.getTranslatedMessage("no-permission"));
			return true;
		}
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
		if (TownyElections.getTownElection(p) != null) {
			p.sendMessage(TownyElections.getTranslatedMessage("active-election"));
		}
		long finishTime = 0;
		try {
			finishTime = Integer.parseInt(args[2]) * 1000 + System.currentTimeMillis();
		} catch (Exception e) {
			p.sendMessage(TownyElections.getTranslatedMessage("error-input-string"));
			return true;
		}
		TownElection e = new TownElection(finishTime, t);
		TownElectionCreateEvent event = new TownElectionCreateEvent(e);
		Bukkit.getPluginManager().callEvent(event);
		if (!event.isCancelled()) {
			TownyElections.addTownElection(e);
			TownyElections.sendTownSubtitle(t, TownyElections.getTranslatedMessage("election-convoked"));
			p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 1);
		}
		return true;
	}

	private boolean executeNationConvoke(CommandSender sender, Command cmd, String str, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(TownyElections.getTranslatedMessage("only-player"));
			return true;
		}
		Player p = (Player) sender;
		if (!(p.hasPermission("townyelections.convoke.nation"))) {
			p.sendMessage(TownyElections.getTranslatedMessage("no-permission"));
			return true;
		}
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
		if (TownyElections.getTownElection(p) != null) {
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
		NationElectionCreateEvent event = new NationElectionCreateEvent(e);
		Bukkit.getPluginManager().callEvent(event);
		if (!event.isCancelled()) {
			TownyElections.addNationElection(e);
			TownyElections.sendNationSubtitle(n, TownyElections.getTranslatedMessage("election-convoked"));
			p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 1);
		}
		return true;
	}

	private boolean executeTownRun(CommandSender sender, Command cmd, String str, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(TownyElections.getTranslatedMessage("only-player"));
			return true;
		}
		Player p = (Player) sender;
		if (!(p.hasPermission("townyelections.run.town"))) {
			p.sendMessage(TownyElections.getTranslatedMessage("no-permission"));
			return true;
		}
		TownElection e = TownyElections.getTownElection(p);
		if (e == null) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-active-election"));
			return true;
		}
		TownElectionRunEvent event = new TownElectionRunEvent(e);
		Bukkit.getPluginManager().callEvent(event);
		if (!event.isCancelled()) {
			e.addCandidate(p.getUniqueId());
			p.sendMessage(TownyElections.getTranslatedMessage("candidate-now"));
			String msg = TownyElections.getTranslatedMessage("new-candidate").replaceAll("%player%", p.getName());
			TownyElections.sendTownSubtitle(e.getTown(), msg);
			p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 1);
		}
		
		return true;
	}

	private boolean executeNationRun(CommandSender sender, Command cmd, String str, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(TownyElections.getTranslatedMessage("only-player"));
			return true;
		}
		Player p = (Player) sender;
		if (!(p.hasPermission("townyelections.run.nation"))) {
			p.sendMessage(TownyElections.getTranslatedMessage("no-permission"));
			return true;
		}
		NationElection e = TownyElections.getNationElection(p);
		if (e == null) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-active-election-nation"));
			return true;
		}
		NationElectionRunEvent event = new NationElectionRunEvent(e);
		Bukkit.getPluginManager().callEvent(event);
		if (!event.isCancelled()) {
			e.addCandidate(p.getUniqueId());
			p.sendMessage(TownyElections.getTranslatedMessage("candidate-now"));
			String msg = TownyElections.getTranslatedMessage("new-candidate").replaceAll("%player%", p.getName());
			TownyElections.sendNationSubtitle(e.getNation(), msg);
			p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 1);
		}
		return true;
	}

	private boolean executeTownVote(CommandSender sender, Command cmd, String str, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(TownyElections.getTranslatedMessage("only-player"));
			return true;
		}
		Player p = (Player) sender;
		if (!(p.hasPermission("townyelections.vote.town"))) {
			p.sendMessage(TownyElections.getTranslatedMessage("no-permission"));
			return true;
		}
		if (args.length <= 2) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-enough-arguments"));
			return true;
		}
		TownElection e;
		e = TownyElections.getTownElection(p);
		if (e == null) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-active-election"));
			return true;
		}
		if (e.hasVoted(p.getUniqueId())) {
			p.sendMessage(TownyElections.getTranslatedMessage("already-voted"));
			return true;
		}
		if (!e.getCandidates().contains(Bukkit.getOfflinePlayer(args[2]).getUniqueId())) {
			p.sendMessage(TownyElections.getTranslatedMessage("invalid-candidate"));
			return true;
		}
		if (System.currentTimeMillis() >= e.getEndTime()) {
			TownyElections.removeTownElection(e);
			String msg = TownyElections.getTranslatedMessage("election-won").replaceAll("%player%",
					Bukkit.getOfflinePlayer(e.getWinner()).getName());
			TownyElections.sendTownSubtitle(e.getTown(), msg);
			return true;
		}
		TownElectionVoteEvent event = new TownElectionVoteEvent(e);
		Bukkit.getPluginManager().callEvent(event);
		if (!event.isCancelled()) {
			e.addVote(p.getUniqueId(), Bukkit.getOfflinePlayer(args[2]).getUniqueId());
			String msg = TownyElections.getTranslatedMessage("you-voted");
			msg = msg.replaceAll("%player", Bukkit.getOfflinePlayer(args[2]).getName());
			p.sendMessage(msg);
			p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 1);
		}
		return true;
	}

	private boolean executeNationVote(CommandSender sender, Command cmd, String str, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(TownyElections.getTranslatedMessage("only-player"));
			return true;
		}
		Player p = (Player) sender;
		if (!(p.hasPermission("townyelections.vote.nation"))) {
			p.sendMessage(TownyElections.getTranslatedMessage("no-permission"));
			return true;
		}
		if (args.length <= 2) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-enough-arguments"));
			return true;
		}
		NationElection e;
		e = TownyElections.getNationElection(p);
		if (e == null) {
			p.sendMessage(TownyElections.getTranslatedMessage("not-active-election-nation"));
			return true;
		}
		if (e.hasVoted(p.getUniqueId())) {
			p.sendMessage(TownyElections.getTranslatedMessage("already-voted"));
			return true;
		}
		if (!e.getCandidates().contains(Bukkit.getOfflinePlayer(args[2]).getUniqueId())) {
			p.sendMessage(TownyElections.getTranslatedMessage("invalid-candidate"));
			return true;
		}
		if (System.currentTimeMillis() >= e.getEndTime()) {
			TownyElections.removeNationElection(e);
			String msg = TownyElections.getTranslatedMessage("election-won").replaceAll("%player%",
					Bukkit.getOfflinePlayer(e.getWinner()).getName());
			TownyElections.sendNationSubtitle(e.getNation(), msg);
			return true;
		}
		NationElectionVoteEvent event = new NationElectionVoteEvent(e);
		Bukkit.getPluginManager().callEvent(event);
		if (!event.isCancelled()) {
			e.addVote(p.getUniqueId(), Bukkit.getOfflinePlayer(args[2]).getUniqueId());
			String msg = TownyElections.getTranslatedMessage("you-voted");
			msg = msg.replaceAll("%player", Bukkit.getOfflinePlayer(args[2]).getName());
			p.sendMessage(msg);
			p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 1);
		}
		return true;
	}

}

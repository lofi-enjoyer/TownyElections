package com.aurgiyalgo.TownyElections.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.aurgiyalgo.TownyElections.TownyElections;
import com.aurgiyalgo.TownyElections.revolutions.Revolution;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;

public class RevolutionsCommandHandler implements CommandExecutor {

	private TownyElections instance;

	public RevolutionsCommandHandler(TownyElections instance) {
		this.instance = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args) {
		if (args.length < 1) {
			return executeHelp(sender, cmd, str, args);
		}
		if (!TownyElections.areRevolutionsEnabled()) {
			sender.sendMessage(TownyElections.getTranslatedMessage("revolutions-disabled"));
			return true;
		}
		Player p = (Player) sender;
		if (!(p.hasPermission("townyelections.revolution"))) {
			p.sendMessage(TownyElections.getTranslatedMessage("no-permission"));
			return true;
		}
		switch (args[0]) {
		case "start":
			try {
				if (TownyUniverse.getInstance().getDataSource().getResident(p.getName()).isMayor() || TownyUniverse.getInstance().getDataSource().getResident(p.getName()).getTown().getAssistants()
						.contains(TownyUniverse.getInstance().getDataSource().getResident(p.getName()))) {
					sender.sendMessage(TownyElections.getTranslatedMessage("is-staff"));
					return true;
				}
				Revolution r = new Revolution(
						TownyUniverse.getInstance().getDataSource().getResident(p.getName()).getTown(),
						p.getUniqueId());
				TownyElections.addRevolution(r);
				sender.sendMessage(TownyElections.getTranslatedMessage("revolution-created"));
				return true;
			} catch (NotRegisteredException e) {
				e.printStackTrace();
			}
			break;
		case "invite":
			Revolution r = TownyElections.getRevolution(p);
			if (r == null)
				return true;

			if (r.getLeader() != p.getUniqueId()) {
				sender.sendMessage(ChatColor.RED + "You are not the leader of the revolution");
				return true;
			}

			if (args.length < 2) {
				sender.sendMessage(TownyElections.getTranslatedMessage("not-enough-arguments"));
				return true;
			}

			Player invited = Bukkit.getPlayer(args[1]);

			try {
				if (!r.getTown().equals(TownyUniverse.getInstance().getDataSource().getResident(invited.getName())
						.getTown())) {
					sender.sendMessage(TownyElections.getTranslatedMessage("not-in-a-town"));
					return true;
				}
			} catch (NotRegisteredException e) {
				e.printStackTrace();
				return true;
			}

			TownyElections.revolutionInvite(invited.getUniqueId(), r);
			invited.sendMessage(TownyElections.getTranslatedMessage("revolution-invited"));
			break;
		case "disband":
			Revolution rev = TownyElections.getRevolution(p);
			if (rev == null)
				return true;

			if (rev.getLeader() != p.getUniqueId())
				return true;

			TownyElections.disbandRevolution(rev);
			break;
		case "accept":
			Revolution revol = TownyElections.getRevolution(p);
			if (revol == null)
				return true;

			TownyElections.getInvite(p.getUniqueId()).addMember(p.getUniqueId());

			for (UUID player : revol.getMembers()) {
				Player pl = Bukkit.getPlayer(player);
				if (player == null)
					continue;
				pl.sendMessage(TownyElections.getTranslatedMessage("revolution-joined"));
			}
			Player pl = Bukkit.getPlayer(revol.getLeader());
			if (pl != null)
				pl.sendMessage(TownyElections.getTranslatedMessage("revolution-joined"));
			break;
		}
		return true;
	}

	private boolean executeHelp(CommandSender sender, Command cmd, String str, String[] args) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', instance.getHelpMessage()));
		return true;
	}

}

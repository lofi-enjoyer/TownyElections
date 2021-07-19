package com.aurgiyalgo.TownyElections.commands.party;

import com.aurgiyalgo.TownyElections.TownyElections;
import com.aurgiyalgo.TownyElections.commands.CommandHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class PartyCommandHandler extends CommandHandler {

	public PartyCommandHandler() {
		addSubCommand(new PartyCreateSubCommand());
		addSubCommand(new PartyLeaveSubCommand());
		addSubCommand(new PartyAddSubCommand());
		addSubCommand(new PartyAcceptSubCommand());
		addSubCommand(new PartyInvitesSubCommand());
		addSubCommand(new PartySetLeaderSubCommand());
		addSubCommand(new PartyPromoteSubCommand());
		addSubCommand(new PartyDemoteSubCommand());
		addSubCommand(new PartyInfoSubCommand());
	}

	@Override
	protected boolean executeHelp(CommandSender sender) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', TownyElections.Text.PARTY_HELP_MESSAGE));
		return true;
	}

}

package com.aurgiyalgo.TownyElections.commands.party;

import com.aurgiyalgo.TownyElections.commands.CommandHandler;

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

}

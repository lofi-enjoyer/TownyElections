package com.aurgiyalgo.TownyElections.commands;

import com.aurgiyalgo.TownyElections.TownyElections;
import com.aurgiyalgo.TownyElections.elections.ElectionManager;
import com.aurgiyalgo.TownyElections.government.GovernmentManager;
import com.aurgiyalgo.TownyElections.parties.PartyManager;
import org.bukkit.entity.Player;

public abstract class SubCommand {

    private final String name;
    private final int minimumArguments;

    protected final PartyManager partyManager;
    protected final ElectionManager electionManager;
    protected final GovernmentManager governmentManager;

    public SubCommand(String name, int minimumArguments) {
        this.name = name;
        this.minimumArguments = minimumArguments;

        this.partyManager = TownyElections.getInstance().getPartyManager();
        this.electionManager = TownyElections.getInstance().getElectionManager();
        this.governmentManager = TownyElections.getInstance().getGovernmentManager();
    }

    public abstract boolean execute(Player player, String[] args);

    public String getName() {
        return this.name;
    }

    public int getMinimumArguments() {
        return minimumArguments;
    }
}

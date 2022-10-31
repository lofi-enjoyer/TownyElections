package me.lofienjoyer.TownyElections.commands;

import me.lofienjoyer.TownyElections.TownyElections;
import me.lofienjoyer.TownyElections.elections.ElectionManager;
import me.lofienjoyer.TownyElections.government.GovernmentManager;
import me.lofienjoyer.TownyElections.parties.PartyManager;
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

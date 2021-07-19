package com.aurgiyalgo.TownyElections.commands;

import com.aurgiyalgo.TownyElections.TownyElections;
import com.aurgiyalgo.TownyElections.parties.PartyManager;
import org.bukkit.entity.Player;

public abstract class SubCommand {

    private String name;
    private int minimumArguments;
    protected PartyManager partyManager;

    public SubCommand(String name, int minimumArguments) {
        this.partyManager = TownyElections.getInstance().getPartyManager();

        this.minimumArguments = minimumArguments;
    }

    public abstract boolean execute(Player player, String[] args);

    public String getName() {
        return this.name;
    }

    public int getMinimumArguments() {
        return minimumArguments;
    }
}

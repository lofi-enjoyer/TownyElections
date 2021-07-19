package com.aurgiyalgo.TownyElections.commands.elections;

import com.aurgiyalgo.TownyElections.commands.SubCommand;
import org.bukkit.entity.Player;

public class ElectionsVoteSubCommand extends SubCommand {

    public ElectionsVoteSubCommand() {
        super("vote", 2);
    }

    @Override
    public boolean execute(Player player, String[] args) {
        return false;
    }

}

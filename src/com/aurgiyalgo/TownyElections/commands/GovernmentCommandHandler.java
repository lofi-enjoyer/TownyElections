package com.aurgiyalgo.TownyElections.commands;

import com.aurgiyalgo.TownyElections.commands.government.GovernmentListSubCommand;
import org.bukkit.command.CommandSender;

public class GovernmentCommandHandler extends CommandHandler {

    public GovernmentCommandHandler() {
        addSubCommand(new GovernmentListSubCommand());
    }

    @Override
    protected boolean executeHelp(CommandSender sender) {
        return false;
    }

}

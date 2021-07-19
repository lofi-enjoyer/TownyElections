package com.aurgiyalgo.TownyElections.commands;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static com.aurgiyalgo.TownyElections.TownyElections.getMessage;

public abstract class CommandHandler implements CommandExecutor {

    private List<SubCommand> subCommands;

    public CommandHandler() {
        this.subCommands = new ArrayList<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!isPlayer(sender)) return true;

        Player player = (Player) sender;

        for (SubCommand subCommand : subCommands) {

            if (!args[0].equalsIgnoreCase(subCommand.getName()))
                continue;

            if (args.length -1 < subCommand.getMinimumArguments())
                return notEnoughArguments(sender);

            if (!subCommand.execute(player, (String[]) ArrayUtils.remove(args, 0)))
                return executeHelp(sender);

            return true;
        }

        return executeHelp(sender);
    }

    protected void addSubCommand(SubCommand subCommand) {
        subCommands.add(subCommand);
    }

    protected abstract boolean executeHelp(CommandSender sender);

    private boolean notEnoughArguments(CommandSender sender) {
        sender.sendMessage(getMessage("not-enough-arguments"));
        return true;
    }

    private boolean isPlayer(CommandSender sender) {
        if (sender instanceof Player) return true;
        sender.sendMessage(getMessage("only-player"));
        return false;
    }

}

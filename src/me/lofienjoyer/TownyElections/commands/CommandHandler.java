package me.lofienjoyer.TownyElections.commands;

import me.lofienjoyer.TownyElections.TownyElections;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class CommandHandler implements CommandExecutor {

    private List<SubCommand> subCommands;

    public CommandHandler() {
        this.subCommands = new ArrayList<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) return executeHelp(sender);

        if (!isPlayer(sender)) return true;

        Player player = (Player) sender;

        for (SubCommand subCommand : subCommands) {

            if (!args[0].equalsIgnoreCase(subCommand.getName()))
                continue;

            if (args.length -1 < subCommand.getMinimumArguments())
                return notEnoughArguments(sender);

            String[] subArgs = new String[args.length - 1];
            if (args.length - 2 >= 0) System.arraycopy(args, 1, subArgs, 0, args.length - 1);

            if (!subCommand.execute(player, subArgs))
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
        sender.sendMessage(TownyElections.getMessage("not-enough-arguments"));
        return true;
    }

    private boolean isPlayer(CommandSender sender) {
        if (sender instanceof Player) return true;
        sender.sendMessage(TownyElections.getMessage("only-player"));
        return false;
    }

}

package me.lofienjoyer.TownyElections.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class TElectTabCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String str, String[] args) {
		if (args.length == 1) {
			String[] subCommands = {"info"};
			
			List<String> cmdList = new ArrayList<String>();
			for (String s : subCommands) {
				if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
					cmdList.add(s);
				}
			}
			return cmdList;
		}
		return null;
	}

}

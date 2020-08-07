package com.aurgiyalgo.TownyElections.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.aurgiyalgo.TownyElections.TownyElections;

public class PartyCommandHandler implements CommandExecutor {

	private TownyElections instance;

	public PartyCommandHandler(TownyElections instance) {
		this.instance = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args) {
		sender.sendMessage(ChatColor.RED + "Work In Progress...");
		return true;
	}

}

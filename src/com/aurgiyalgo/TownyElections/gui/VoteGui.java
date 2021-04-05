package com.aurgiyalgo.TownyElections.gui;

import java.util.List;

import com.aurgiyalgo.TownyElections.elections.Election;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.aurgiyalgo.TownyElections.TownyElections;
import com.aurgiyalgo.TownyElections.elections.TownElection;
import com.aurgiyalgo.TownyElections.parties.TownParty;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import net.md_5.bungee.api.ChatColor;

public abstract class VoteGui implements InventoryProvider {

	@Override
	public void init(Player player, InventoryContents contents) {
		Pagination pagination = contents.pagination();
		setItems(player, contents);

		contents.set(3, 2, ClickableItem.of(new ItemStack(Material.ARROW),
				e -> getInventory().open(player, pagination.previous().getPage())));
		contents.set(3, 6, ClickableItem.of(new ItemStack(Material.ARROW),
				e -> getInventory().open(player, pagination.next().getPage())));
	}

	@Override
	public void update(Player player, InventoryContents contents) {

	}

	public abstract void setItems(Player player, InventoryContents contents);

	public abstract SmartInventory getInventory();

}

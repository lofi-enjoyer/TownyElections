package com.aurgiyalgo.TownyElections.gui;

import java.util.List;

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

public class VoteGui implements InventoryProvider {

	public static final SmartInventory INVENTORY = SmartInventory.builder()
			.manager(TownyElections.getInstance().getInventoryManager())
			.id("votegui")
			.provider(new VoteGui())
			.size(3, 9)
			.title(ChatColor.GOLD + "Vote")
			.build();

	@Override
	public void init(Player player, InventoryContents contents) {
		Town t;
		try {
			t = TownyUniverse.getInstance().getDataSource().getResident(player.getName()).getTown();
		} catch (NotRegisteredException e1) {
			e1.printStackTrace();
			player.sendMessage(ChatColor.RED + "Error while loading the menu!");
			return;
		}
		
		List<TownParty> townPartyList = TownyElections.getInstance().getPartyManager().getPartiesForTown(t.getName());
		
		TownElection election;
		election = TownyElections.getInstance().getElectionManager().getTownElection(player);
		
		for (int i = 0; i < townPartyList.size(); i++) {
			int x = i / 9;
			int y = i % 9;
			ItemStack item = new ItemStack(Material.PAPER);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_BLUE + townPartyList.get(i).getName());
			item.setItemMeta(meta);
			final int it = i;
			contents.set(x, y, ClickableItem.of(item, e -> {
				election.addVote(player.getUniqueId(), townPartyList.get(it).getName());
				String msg = TownyElections.getTranslatedMessage("you-voted");
				msg = msg.replaceAll("%party%", townPartyList.get(it).getName());
				player.sendMessage(msg);
				player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 1);
			}));
		}
	}

	@Override
	public void update(Player player, InventoryContents contents) {

	}

}

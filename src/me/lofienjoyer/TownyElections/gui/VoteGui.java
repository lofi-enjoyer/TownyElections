package me.lofienjoyer.TownyElections.gui;

import fr.minuskube.inv.content.Pagination;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;

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

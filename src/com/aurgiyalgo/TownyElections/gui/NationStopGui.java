package com.aurgiyalgo.TownyElections.gui;

import com.aurgiyalgo.TownyElections.TownyElections;
import com.aurgiyalgo.TownyElections.elections.NationElection;
import com.aurgiyalgo.TownyElections.elections.TownElection;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class NationStopGui implements InventoryProvider {

    public static SmartInventory INVENTORY = SmartInventory.builder()
            .manager(TownyElections.getInstance().getInventoryManager())
            .id("nationstopgui")
            .provider(new NationStopGui())
            .size(3, 9)
            .title(ChatColor.RED + "Stop the election")
            .build();

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fill(ClickableItem.empty(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)));

        contents.set(1, 4, ClickableItem.of(getItem(), e -> {
            NationElection election = TownyElections.getInstance().getElectionManager().getNationElection(player);
            TownyElections.getInstance().getElectionManager().removeNationElection(election);
            player.sendMessage(ChatColor.RED + "Election was stopped!");
            player.closeInventory();
        }));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }

    private ItemStack getItem() {
        ItemStack item = new ItemStack(Material.RED_BANNER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Stop the election");
        meta.setLore(Arrays.asList("", ChatColor.GRAY + "Click to stop the ongoing election"));
        item.setItemMeta(meta);
        return item;
    }

}

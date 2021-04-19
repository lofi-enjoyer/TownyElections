package com.aurgiyalgo.TownyElections.gui;

import com.aurgiyalgo.TownyElections.TownyElections;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class TownStopGui implements InventoryProvider {

    public static SmartInventory INVENTORY = SmartInventory.builder()
            .manager(TownyElections.getInstance().getInventoryManager())
            .id("townstopgui")
            .provider(new TownStopGui())
            .size(3, 9)
            .title(ChatColor.RED + "Stop the election")
            .build();

    @Override
    public void init(Player player, InventoryContents contents) {

    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }

}

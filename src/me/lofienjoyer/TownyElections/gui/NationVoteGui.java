package me.lofienjoyer.TownyElections.gui;

import me.lofienjoyer.TownyElections.TownyElections;
import me.lofienjoyer.TownyElections.elections.NationElection;
import me.lofienjoyer.TownyElections.parties.NationParty;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class NationVoteGui extends VoteGui {

    public static SmartInventory INVENTORY = SmartInventory.builder()
            .manager(TownyElections.getInstance().getInventoryManager())
            .id("nationvotegui")
            .provider(new NationVoteGui())
            .size(4, 9)
            .title(ChatColor.BLUE + "Vote in your nation election!")
            .build();

    @Override
    public void setItems(Player player, InventoryContents contents) {
        Nation n;
        try {
            n = TownyUniverse.getInstance().getResident(player.getName()).getTown().getNation();
        } catch (NotRegisteredException e1) {
            e1.printStackTrace();
            player.sendMessage(ChatColor.RED + "Error while loading the menu!");
            return;
        }

        List<NationParty> nationPartyList = TownyElections.getInstance().getPartyManager().getPartiesForNation(n.getName());

        NationElection election;
        election = TownyElections.getInstance().getElectionManager().getNationElection(player);

        Pagination pagination = contents.pagination();

        ClickableItem[] items = new ClickableItem[nationPartyList.size()];

        for (int i = 0; i < items.length; i++) {
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.YELLOW + nationPartyList.get(i).getName());
            item.setItemMeta(meta);
            final int it = i;
            items[i] = ClickableItem.of(item, e -> {
                election.addVote(player.getUniqueId(), nationPartyList.get(it).getName());
                player.closeInventory();
                String msg = TownyElections.getMessage("you-voted");
                msg = msg.replaceAll("%party%", nationPartyList.get(it).getName());
                player.sendMessage(msg);
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 2);
            });
        }

        pagination.setItems(items);
        pagination.setItemsPerPage(18);

        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 0, 0));
    }

    @Override
    public SmartInventory getInventory() {
        return INVENTORY;
    }
}

package me.lofienjoyer.TownyElections.parties;

import java.util.ArrayList;
import java.util.UUID;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;

public class TownParty extends Party {
	
	private Town town;

	public TownParty(String name, UUID leader, UUID territory) {
		super(name, leader, PartyType.TOWN, territory);
		
		setup();
	}
	
	public void setup() {
		if (members == null) members = new ArrayList<UUID>();
		if (assistants == null) assistants = new ArrayList<UUID>();
		if (invites == null) invites = new ArrayList<UUID>();
		
		try {
			town = TownyUniverse.getInstance().getDataSource().getTown(territory);
		} catch (NotRegisteredException e) {}
	}
	
	public Town getTown() {
		return town;
	}

}

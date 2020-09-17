package com.aurgiyalgo.TownyElections.parties;

import java.util.UUID;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;

public class TownParty extends Party {
	
	private Town town;

	public TownParty(String name, UUID leader) {
		super(name, leader, PartyType.TOWN);
		
		setup();
	}
	
	public void setup() {
		try {
			town = TownyUniverse.getInstance().getDataSource().getTown(territory);
		} catch (NotRegisteredException e) {}
	}
	
	public Town getTown() {
		return town;
	}

}

package com.aurgiyalgo.TownyElections.parties;

import java.util.UUID;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;

public class NationParty extends Party {
	
	private Nation nation;

	public NationParty(String name, UUID leader) {
		super(name, leader, PartyType.NATION);
		
		try {
			nation = TownyUniverse.getInstance().getDataSource().getNation(territory);
		} catch (NotRegisteredException e) {}
	}
	
	public Nation getNation() {
		return nation;
	}

}

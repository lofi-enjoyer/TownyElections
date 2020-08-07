package com.aurgiyalgo.TownyElections.parties;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PartiesManager {
	
	private List<Party> parties;
	
	public PartiesManager() {
		parties = new ArrayList<Party>();
	}
	
	public void addParty(Party party) {
		if (!parties.contains(party)) parties.add(party);
	}
	
	public void removeParty(Party party) {
		parties.remove(party);
	}
	
	public void removeParty(String partyName) {
		Iterator<Party> iterator = parties.iterator();
		while (iterator.hasNext()) {
			Party party = iterator.next();
			if (!party.getName().equals(partyName)) continue;
			iterator.remove();
			return;
		}
	}

}

package com.aurgiyalgo.TownyElections.parties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class Party {
	
	public static enum PartyType {
		TOWN("TOWN"), NATION("NATION");
		
		private static Map<String, PartyType> lookup = new HashMap<String, PartyType>();
		
		static {
			for (PartyType pt : values()) {
				lookup.put(pt.type, pt);
			}
		}
		
		private String type;
		
		private PartyType(String type) {
			this.type = type;
		}
		
		public static PartyType getPartyType(String type) {
			return lookup.get(type);
		}
	}
	
	protected String name;
	protected UUID leader;
	protected List<UUID> members;
	protected PartyType partyType;
	protected UUID territory;
	
	public Party(String name, UUID leader, PartyType partyType) {
		members = new ArrayList<UUID>();
		
		this.name = name;
		this.leader = leader;
		this.partyType = partyType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UUID getLeader() {
		return leader;
	}

	public void setLeader(UUID leader) {
		this.leader = leader;
	}
	
	public void addMember(UUID member) {
		if (members.contains(member)) return;
		members.add(member);
	}
	
	public void removeMember(UUID member) {
		members.remove(member);
	}

	public List<UUID> getMembers() {
		return members;
	}

	public void setMembers(List<UUID> members) {
		this.members = members;
	}
	
	public PartyType getType() {
		return partyType;
	}

}

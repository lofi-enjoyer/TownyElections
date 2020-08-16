package com.aurgiyalgo.TownyElections.revolutions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;

import com.google.gson.annotations.Expose;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

public class Revolution {
	
	private UUID townUuid;
	private Town town;
	@Expose
	private UUID leader;
	@Expose
	private List<UUID> members;
	@Expose
	private List<UUID> killedStaff;
	
	public Revolution(Town town, UUID leader) {
		this.town = town;
		this.leader = leader;
		this.members = new ArrayList<UUID>();
		this.killedStaff = new ArrayList<UUID>();
		
		this.townUuid = town.getUuid();
	}
	
	public void setup() {
		try {
			town = TownyUniverse.getInstance().getDataSource().getTown(townUuid);
		} catch (NotRegisteredException e) {
			e.printStackTrace();
		}
	}
	
	public void finishRevolution() {
		Resident newMayor;
		Resident oldMayor;
		try {
			oldMayor = town.getMayor();
			newMayor = TownyUniverse.getInstance().getDataSource().getResident(Bukkit.getOfflinePlayer(leader).getName());
			town.setMayor(newMayor);
			town.removeResident(oldMayor);
			for (Resident res : town.getAssistants()) {
				town.removeResident(res);
			}
//			TownyUniverse.getInstance().getDataSource().saveTown(town);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addMember(UUID member) {
		if (!members.contains(member)) members.add(member);
	}
	
	public void removeMember(UUID member) {
		if (members.contains(member)) members.remove(member);
	}
	
	public void addKilledStaff(UUID player) {
		if (!killedStaff.contains(player)) killedStaff.add(player);
	}
	
	public void removeKilledStaff(UUID player) {
		if (killedStaff.contains(player)) killedStaff.add(player);
	}
	
	public UUID getLeader() {
		return leader;
	}
	
	public List<UUID> getMembers() {
		return members;
	}
	
	public Town getTown() {
		return town;
	}
	
	public List<UUID> getKilledStaff() {
		return killedStaff;
	}

}

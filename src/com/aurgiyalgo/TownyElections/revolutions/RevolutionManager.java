package com.aurgiyalgo.TownyElections.revolutions;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.aurgiyalgo.TownyElections.TownyElections;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Town;

public class RevolutionManager {
	
	private List<Revolution> revolutions;
	private Map<UUID, Revolution> invites;
	private boolean enabledRevolutions;
	
	public RevolutionManager(TownyElections instance) {
		this.revolutions = new ArrayList<Revolution>();
		this.invites = new HashMap<UUID, Revolution>();
		this.enabledRevolutions = true;
	}
	
	public void loadRevolutions(File dataFolder) throws Exception {
		if (!dataFolder.exists()) {
			return;
		}
		File f = new File(dataFolder, "revolutions.json");
		if (!f.exists()) {
			return;
		}

		JSONParser parser = new JSONParser();

		Object obj = parser.parse(new FileReader(f));
		JSONObject jsonFile = (JSONObject) obj;

		JSONArray revolutionsArray = (JSONArray) jsonFile.get("revolutions");

		for (int i = 0; i < revolutionsArray.size(); i++) {
			JSONObject revolution = (JSONObject) revolutionsArray.get(i);

			Town town = TownyUniverse.getInstance().getDataSource().getTown(revolution.get("town").toString());
			UUID leader = UUID.fromString(revolution.get("leader").toString());
			
			Revolution rev = new Revolution(town, leader);
			JSONArray members = (JSONArray) revolution.get("members");
			for (int j = 0; j < members.size(); j++) {
				rev.addMember(UUID.fromString(members.get(j).toString()));
			}

			JSONArray killedStaff = (JSONArray) revolution.get("killedstaff");
			for (int j = 0; j < killedStaff.size(); j++) {
				rev.addKilledStaff(UUID.fromString(killedStaff.get(j).toString()));
			}
		}
	}
	
	public void saveRevolutions(File dataFolder) throws Exception {
		if (!dataFolder.exists()) {
			dataFolder.mkdir();
		}
		File f = new File(dataFolder, "revolutions.json");
		if (f.exists()) {
			f.delete();
		}
		f.createNewFile();

		JSONObject jsonFile = new JSONObject();

		JSONArray revolutionsArray = new JSONArray();

		for (int i = 0; i < revolutions.size(); i++) {
			JSONObject electionObject = new JSONObject();
			electionObject.put("town", revolutions.get(i).getTown().getName());
			electionObject.put("leader", revolutions.get(i).getLeader());
			
			JSONArray members = new JSONArray();
			for (UUID player : revolutions.get(i).getMembers()) {
				members.add(player.toString());
			}
			electionObject.put("members", members);
			
			JSONArray killedStaff = new JSONArray();
			for (UUID player : revolutions.get(i).getKilledStaff()) {
				killedStaff.add(player.toString());
			}
			electionObject.put("killedstaff", killedStaff);

			revolutionsArray.add(electionObject);
		}

		jsonFile.put("elections", revolutionsArray);

		FileWriter fw = new FileWriter(f);
		fw.write(jsonFile.toString());
		fw.close();
	}
	
	public Revolution getRevolution(Player p) {
		for (Revolution r : revolutions) {
			if (r.getLeader() == p.getUniqueId()) return r;
			
			if (r.getMembers().contains(p.getUniqueId())) return r;
		}
		return null;
	}
	
	public void addRevolution(Revolution r) {
		if (revolutions.contains(r)) return;
		revolutions.add(r);
	}
	
	public void finishRevolution(Revolution r) {
		r.finishRevolution();
		revolutions.remove(r);
		removeTownRevolutions(r.getTown());
	}
	
	public void removeTownRevolutions(Town t) {
		Iterator<Revolution> iterator = revolutions.iterator();
		while (iterator.hasNext()) {
			Revolution r = iterator.next();
			if (r.getTown().equals(t)) iterator.remove();
		}
	}
	
	public Revolution getInvite(UUID player) {
		if (!invites.containsKey(player)) return null;
		
		return invites.get(player);
	}
	
	public void revolutionInvite(UUID player, Revolution r) {
		if (invites.containsKey(player)) invites.remove(player);
		
		invites.put(player, r);
	}
	
	public List<Revolution> getRevolutions() {
		return revolutions;
	}
	
	public void setRevolutionsEnabled(boolean enabled) {
		enabledRevolutions = enabled;
	}
	
	public boolean areRevolutionsEnabled() {
		return enabledRevolutions;
	}

}
